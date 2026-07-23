package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdmissionWaitingListService {

    private final AdmissionWaitingListRepository waitingListRepository;
    private final AdmissionWaitingListEntryRepository entryRepository;
    private final AdmissionTestAttemptRepository attemptRepository;
    private final AdmissionTestRepository testRepository;
    private final AdmissionMeritListRepository meritListRepository;

    public Page<AdmissionWaitingList> findAll(Pageable pageable) {
        return waitingListRepository.findAll(pageable);
    }

    public Page<AdmissionWaitingList> findByFilters(String search, String status, Long sessionId,
            Long facultyId, Long programId, Long testId, Pageable pageable) {
        return waitingListRepository.findByFilters(search, status, sessionId, facultyId,
                programId, testId, pageable);
    }

    public AdmissionWaitingList findById(Long id) {
        return waitingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WaitingList", "id", id));
    }

    public AdmissionWaitingList save(AdmissionWaitingList waitingList) {
        return waitingListRepository.save(waitingList);
    }

    public AdmissionWaitingList update(Long id, AdmissionWaitingList waitingList) {
        findById(id);
        waitingList.setId(id);
        return waitingListRepository.save(waitingList);
    }

    public void delete(Long id) {
        AdmissionWaitingList list = findById(id);
        if ("PUBLISHED".equals(list.getStatus())) {
            throw new BusinessException("Cannot delete a published waiting list.");
        }
        entryRepository.deleteAll(entryRepository.findByWaitingList_IdOrderByRankAsc(id));
        waitingListRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return waitingListRepository.countByStatus(status);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", waitingListRepository.count());
        stats.put("draft", waitingListRepository.countByStatus("DRAFT"));
        stats.put("published", waitingListRepository.countByStatus("PUBLISHED"));
        return stats;
    }

    @Transactional
    public AdmissionWaitingList generateWaitingList(Long testId, String listName, Integer totalSlots,
            String academicYear, Long facultyId, Long programId) {
        AdmissionTest test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTest", "id", testId));

        List<AdmissionTestAttempt> gradedAttempts = attemptRepository
                .findByTest_IdAndStatusOrderByScoreDesc(testId, "GRADED");

        int meritCutoff = 0;
        List<AdmissionMeritList> existingLists = meritListRepository.findByTest_Id(testId);
        for (AdmissionMeritList ml : existingLists) {
            if (ml.getTotalSeats() != null && ml.getTotalSeats() > meritCutoff) {
                meritCutoff = ml.getTotalSeats();
            }
        }

        AdmissionWaitingList waitingList = AdmissionWaitingList.builder()
                .name(listName != null ? listName : test.getName() + " - Waiting List")
                .description("Auto-generated waiting list for " + test.getName())
                .academicYear(academicYear != null ? academicYear : test.getAcademicYear())
                .session(test.getSession())
                .faculty(facultyId != null ? getFaculty(facultyId) : test.getFaculty())
                .department(test.getDepartment())
                .program(programId != null ? getProgram(programId) : test.getProgram())
                .shift(test.getShift())
                .test(test)
                .totalSlots(totalSlots)
                .status("DRAFT")
                .build();
        waitingList = waitingListRepository.save(waitingList);

        int position = 1;
        for (int i = 0; i < gradedAttempts.size(); i++) {
            AdmissionTestAttempt attempt = gradedAttempts.get(i);
            if (i < meritCutoff) continue;

            PreAdmissionRegistration reg = attempt.getRegistration();

            AdmissionWaitingListEntry entry = AdmissionWaitingListEntry.builder()
                    .waitingList(waitingList)
                    .registration(reg)
                    .rank(position)
                    .rollNumber("W" + String.format("%04d", position))
                    .applicationNumber(reg.getRegistrationNumber())
                    .applicantName(reg.getFirstName() + " " + reg.getLastName())
                    .score(attempt.getScore())
                    .testMarks(attempt.getScore())
                    .totalWeightedScore(attempt.getPercentage())
                    .status("WAITING")
                    .build();
            entryRepository.save(entry);
            position++;
        }

        waitingList.setTotalApplicants(position - 1);
        if (totalSlots != null) {
            waitingList.setCutoffScore(gradedAttempts.size() > meritCutoff ?
                    gradedAttempts.get(meritCutoff).getScore() : 0.0);
        }

        return waitingListRepository.save(waitingList);
    }

    @Transactional
    public AdmissionWaitingList publish(Long id) {
        AdmissionWaitingList waitingList = findById(id);
        waitingList.setStatus("PUBLISHED");
        waitingList.setPublishedAt(LocalDateTime.now());
        return waitingListRepository.save(waitingList);
    }

    @Transactional
    public AdmissionWaitingList unpublish(Long id) {
        AdmissionWaitingList waitingList = findById(id);
        waitingList.setStatus("DRAFT");
        waitingList.setPublishedAt(null);
        return waitingListRepository.save(waitingList);
    }

    private Faculty getFaculty(Long id) {
        Faculty f = new Faculty();
        f.setId(id);
        return f;
    }

    private Program getProgram(Long id) {
        Program p = new Program();
        p.setId(id);
        return p;
    }
}
