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
@Transactional(readOnly = true)
public class AdmissionMeritListService {

    private final AdmissionMeritListRepository meritListRepository;
    private final AdmissionMeritListEntryRepository entryRepository;
    private final AdmissionTestAttemptRepository attemptRepository;
    private final AdmissionTestResultRepository testResultRepository;
    private final AdmissionTestRepository testRepository;
    private final PreAdmissionRegistrationRepository registrationRepository;
    private final NotificationHelper notificationHelper;

    public Page<AdmissionMeritList> findAll(Pageable pageable) {
        return meritListRepository.findAll(pageable);
    }

    public Page<AdmissionMeritList> findByFilters(String search, String status, Long sessionId,
            Long facultyId, Long departmentId, Long programId, Long testId, Pageable pageable) {
        return meritListRepository.findByFilters(search, status, sessionId, facultyId,
                departmentId, programId, testId, pageable);
    }

    public AdmissionMeritList findById(Long id) {
        return meritListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MeritList", "id", id));
    }

    @Transactional
    public AdmissionMeritList save(AdmissionMeritList meritList) {
        return meritListRepository.save(meritList);
    }

    @Transactional
    public AdmissionMeritList update(Long id, AdmissionMeritList meritList) {
        findById(id);
        meritList.setId(id);
        return meritListRepository.save(meritList);
    }

    @Transactional
    public void delete(Long id) {
        AdmissionMeritList list = findById(id);
        if ("PUBLISHED".equals(list.getStatus())) {
            throw new BusinessException("Cannot delete a published merit list. Unpublish it first.");
        }
        entryRepository.deleteAll(entryRepository.findByMeritList_IdOrderByRankAsc(id));
        meritListRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return meritListRepository.countByStatus(status);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", meritListRepository.count());
        stats.put("draft", meritListRepository.countByStatus("DRAFT"));
        stats.put("published", meritListRepository.countByStatus("PUBLISHED"));
        stats.put("archived", meritListRepository.countByStatus("ARCHIVED"));
        return stats;
    }

    @Transactional
    public AdmissionMeritList generateMeritList(Long testId, String listName, Integer totalSeats,
            String academicYear, Long facultyId, Long departmentId, Long programId, String shift) {
        AdmissionTest test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTest", "id", testId));

        List<AdmissionTestAttempt> gradedAttempts = attemptRepository
                .findByTest_IdAndStatusOrderByScoreDesc(testId, "GRADED");

        AdmissionMeritList meritList = AdmissionMeritList.builder()
                .name(listName != null ? listName : test.getName() + " - Merit List")
                .description("Auto-generated merit list for " + test.getName())
                .academicYear(academicYear != null ? academicYear : test.getAcademicYear())
                .session(test.getSession())
                .faculty(facultyId != null ? getFaculty(facultyId) : test.getFaculty())
                .department(departmentId != null ? getDepartment(departmentId) : test.getDepartment())
                .program(programId != null ? getProgram(programId) : test.getProgram())
                .shift(shift != null ? shift : test.getShift())
                .test(test)
                .totalSeats(totalSeats)
                .status("DRAFT")
                .build();
        meritList = meritListRepository.save(meritList);

        List<AdmissionMeritListEntry> entries = new ArrayList<>();
        int rank = 1;

        for (AdmissionTestAttempt attempt : gradedAttempts) {
            if (totalSeats != null && rank > totalSeats) break;

            PreAdmissionRegistration reg = attempt.getRegistration();

            Double academicScore = calculateAcademicScore(reg);
            Double totalWeighted = calculateTotalWeightedScore(attempt.getScore(), academicScore, test.getTotalMarks());

            AdmissionMeritListEntry entry = AdmissionMeritListEntry.builder()
                    .meritList(meritList)
                    .registration(reg)
                    .rank(rank)
                    .rollNumber("R" + String.format("%04d", rank))
                    .applicationNumber(reg.getRegistrationNumber())
                    .applicantName(reg.getFirstName() + " " + reg.getLastName())
                    .facultyName(test.getFaculty() != null ? test.getFaculty().getName() : "N/A")
                    .departmentName(test.getDepartment() != null ? test.getDepartment().getName() : "N/A")
                    .programName(test.getProgram() != null ? test.getProgram().getName() : "N/A")
                    .shift(test.getShift())
                    .testMarks(attempt.getScore())
                    .testMaxMarks(attempt.getMaxScore())
                    .score(attempt.getScore())
                    .academicScore(academicScore)
                    .totalWeightedScore(totalWeighted)
                    .sscGpa(reg.getSscGpa())
                    .hscGpa(reg.getHscGpa())
                    .quotaType("GENERAL")
                    .status(rank <= (totalSeats != null ? totalSeats : Integer.MAX_VALUE) ? "SELECTED" : "WAITING")
                    .submittedAt(attempt.getSubmittedAt())
                    .build();
            entries.add(entryRepository.save(entry));
            rank++;
        }

        meritList.setTotalApplicants(gradedAttempts.size());
        meritList.setSelectedCount((int) entries.stream().filter(e -> "SELECTED".equals(e.getStatus())).count());
        meritList.setWaitingCount((int) entries.stream().filter(e -> "WAITING".equals(e.getStatus())).count());

        if (!entries.isEmpty()) {
            meritList.setCutoffScore(entries.stream()
                    .mapToDouble(e -> e.getTotalWeightedScore() != null ? e.getTotalWeightedScore() : 0.0)
                    .min().orElse(0.0));
        }

        return meritListRepository.save(meritList);
    }

    @Transactional
    public AdmissionMeritList publish(Long id, String publishedBy) {
        AdmissionMeritList meritList = findById(id);
        if ("PUBLISHED".equals(meritList.getStatus())) {
            throw new BusinessException("Merit list is already published");
        }
        meritList.setStatus("PUBLISHED");
        meritList.setPublishedAt(LocalDateTime.now());
        meritList.setPublishedBy(publishedBy);
        return meritListRepository.save(meritList);
    }

    @Transactional
    public AdmissionMeritList unpublish(Long id) {
        AdmissionMeritList meritList = findById(id);
        if (!"PUBLISHED".equals(meritList.getStatus())) {
            throw new BusinessException("Merit list is not published");
        }
        meritList.setStatus("DRAFT");
        meritList.setPublishedAt(null);
        meritList.setPublishedBy(null);
        return meritListRepository.save(meritList);
    }

    @Transactional
    public AdmissionMeritList archive(Long id) {
        AdmissionMeritList meritList = findById(id);
        meritList.setStatus("ARCHIVED");
        return meritListRepository.save(meritList);
    }

    private Double calculateAcademicScore(PreAdmissionRegistration reg) {
        double ssc = reg.getSscGpa() != null ? reg.getSscGpa() : 0.0;
        double hsc = reg.getHscGpa() != null ? reg.getHscGpa() : 0.0;
        return (ssc + hsc) / 2.0;
    }

    private Double calculateTotalWeightedScore(Double testScore, Double academicScore, Integer totalMarks) {
        double testWeight = totalMarks != null && totalMarks > 0 ? (testScore / totalMarks) * 70 : 0;
        double academicWeight = academicScore * 30;
        return testWeight + academicWeight;
    }

    private Faculty getFaculty(Long id) {
        Faculty f = new Faculty();
        f.setId(id);
        return f;
    }

    private Department getDepartment(Long id) {
        Department d = new Department();
        d.setId(id);
        return d;
    }

    private Program getProgram(Long id) {
        Program p = new Program();
        p.setId(id);
        return p;
    }
}