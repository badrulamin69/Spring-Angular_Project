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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicantChoiceService {

    private final ApplicantChoiceSubmissionRepository submissionRepository;
    private final ApplicantChoiceRepository choiceRepository;
    private final ChoiceFillingConfigRepository configRepository;
    private final AdmissionMeritListEntryRepository meritEntryRepository;
    private final PreAdmissionRegistrationRepository registrationRepository;
    private final ProgramRepository programRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;

    public Page<ApplicantChoiceSubmission> findAllSubmissions(Pageable pageable) {
        return submissionRepository.findAll(pageable);
    }

    public Page<ApplicantChoiceSubmission> findSubmissionsByFilters(String search, String status, Long configId, Pageable pageable) {
        return submissionRepository.findByFilters(search, status, configId, pageable);
    }

    public ApplicantChoiceSubmission findSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChoiceSubmission", "id", id));
    }

    public ApplicantChoiceSubmission findSubmissionByRegAndConfig(Long registrationId, Long configId) {
        return submissionRepository.findByRegistration_IdAndConfig_Id(registrationId, configId)
                .orElseThrow(() -> new ResourceNotFoundException("ChoiceSubmission", "registrationId", registrationId));
    }

    public List<ApplicantChoice> getChoicesBySubmission(Long submissionId) {
        return choiceRepository.findBySubmission_IdOrderByPriorityAsc(submissionId);
    }

    public List<Map<String, Object>> getAvailablePrograms(Long configId) {
        ChoiceFillingConfig config = configRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("ChoiceFillingConfig", "id", configId));

        List<Program> programs = programRepository.findByIsActive(true);
        List<String> submittedStatuses = Arrays.asList("SUBMITTED", "LOCKED");

        return programs.stream().map(program -> {
            Map<String, Object> map = new HashMap<>();
            map.put("programId", program.getId());
            map.put("programName", program.getName());
            map.put("programCode", program.getCode());
            map.put("programType", program.getProgramType());
            map.put("durationYears", program.getDurationYears());
            map.put("totalCredits", program.getTotalCredits());

            if (program.getDepartment() != null) {
                map.put("departmentId", program.getDepartment().getId());
                map.put("departmentName", program.getDepartment().getName());
                if (program.getDepartment().getFaculty() != null) {
                    map.put("facultyId", program.getDepartment().getFaculty().getId());
                    map.put("facultyName", program.getDepartment().getFaculty().getName());
                }
            }

            long demand = choiceRepository.countByProgramAndConfigAndStatuses(program.getId(), configId, submittedStatuses);
            map.put("currentDemand", demand);

            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    public ApplicantChoiceSubmission getOrCreateSubmission(Long registrationId, Long configId) {
        Optional<ApplicantChoiceSubmission> existing = submissionRepository.findByRegistration_IdAndConfig_Id(registrationId, configId);
        if (existing.isPresent()) {
            return existing.get();
        }

        ChoiceFillingConfig config = configRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("ChoiceFillingConfig", "id", configId));

        if (!"ACTIVE".equals(config.getStatus())) {
            throw new BusinessException("Choice filling is not currently active");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(config.getChoiceStartDate()) || now.isAfter(config.getChoiceEndDate())) {
            throw new BusinessException("Choice submission window is not open");
        }

        PreAdmissionRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("PreAdmissionRegistration", "id", registrationId));

        List<AdmissionMeritListEntry> meritEntries = meritEntryRepository
                .findByRegistration_IdAndStatusIn(registrationId, Arrays.asList("SELECTED", "WAITING"));

        if (meritEntries.isEmpty() && !config.getIncludeWaitingList()) {
            throw new BusinessException("You are not eligible to submit choices. Your merit status does not meet the requirements.");
        }

        ApplicantChoiceSubmission submission = new ApplicantChoiceSubmission();
        submission.setRegistration(registration);
        submission.setConfig(config);
        submission.setApplicantName(registration.getFirstName() + " " + registration.getLastName());
        submission.setSubmissionId(generateSubmissionId(registration));

        if (!meritEntries.isEmpty()) {
            AdmissionMeritListEntry entry = meritEntries.get(0);
            submission.setMeritListEntry(entry);
            submission.setMeritRank(entry.getRank());
            submission.setMeritScore(entry.getTotalWeightedScore());
        }

        return submissionRepository.save(submission);
    }

    @Transactional
    public ApplicantChoice addChoice(Long submissionId, Long programId) {
        ApplicantChoiceSubmission submission = findSubmissionById(submissionId);

        if ("LOCKED".equals(submission.getStatus())) {
            throw new BusinessException("Your submission is locked. Contact admin to reopen.");
        }

        ChoiceFillingConfig config = submission.getConfig();
        if (!isWindowOpen(config) && !"DRAFT".equals(submission.getStatus())) {
            throw new BusinessException("Choice submission window is closed");
        }

        long currentCount = choiceRepository.countBySubmission_Id(submissionId);
        if (currentCount >= config.getMaxChoices()) {
            throw new BusinessException("Maximum number of choices (" + config.getMaxChoices() + ") reached");
        }

        if (choiceRepository.existsBySubmission_IdAndProgram_Id(submissionId, programId)) {
            throw new BusinessException("This program is already in your choice list");
        }

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Program", "id", programId));

        AtomicInteger maxPriority = new AtomicInteger(0);
        choiceRepository.findBySubmission_IdOrderByPriorityAsc(submissionId)
                .forEach(c -> maxPriority.set(Math.max(maxPriority.get(), c.getPriority())));

        ApplicantChoice choice = new ApplicantChoice();
        choice.setSubmission(submission);
        choice.setProgram(program);
        choice.setFaculty(program.getDepartment() != null ? program.getDepartment().getFaculty() : null);
        choice.setDepartment(program.getDepartment());
        choice.setProgramName(program.getName());
        choice.setFacultyName(program.getDepartment() != null && program.getDepartment().getFaculty() != null
                ? program.getDepartment().getFaculty().getName() : "");
        choice.setDepartmentName(program.getDepartment() != null ? program.getDepartment().getName() : "");
        choice.setPriority(maxPriority.get() + 1);
        choice.setStatus("ACTIVE");

        return choiceRepository.save(choice);
    }

    @Transactional
    public void removeChoice(Long choiceId) {
        ApplicantChoice choice = choiceRepository.findById(choiceId)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicantChoice", "id", choiceId));

        ApplicantChoiceSubmission submission = choice.getSubmission();
        if ("LOCKED".equals(submission.getStatus())) {
            throw new BusinessException("Your submission is locked. Contact admin to reopen.");
        }

        choiceRepository.deleteById(choiceId);

        List<ApplicantChoice> remaining = choiceRepository.findBySubmission_IdOrderByPriorityAsc(submission.getId());
        AtomicInteger priority = new AtomicInteger(1);
        remaining.forEach(c -> {
            c.setPriority(priority.getAndIncrement());
            choiceRepository.save(c);
        });

        submission.setTotalChoices(remaining.size());
        submissionRepository.save(submission);
    }

    @Transactional
    public ApplicantChoice moveChoice(Long choiceId, String direction) {
        ApplicantChoice choice = choiceRepository.findById(choiceId)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicantChoice", "id", choiceId));

        ApplicantChoiceSubmission submission = choice.getSubmission();
        if ("LOCKED".equals(submission.getStatus())) {
            throw new BusinessException("Your submission is locked");
        }

        List<ApplicantChoice> allChoices = choiceRepository.findBySubmission_IdOrderByPriorityAsc(submission.getId());
        int currentIndex = allChoices.indexOf(choice);

        if ("up".equals(direction) && currentIndex > 0) {
            ApplicantChoice swap = allChoices.get(currentIndex - 1);
            int tempPriority = choice.getPriority();
            choice.setPriority(swap.getPriority());
            swap.setPriority(tempPriority);
            choiceRepository.save(choice);
            choiceRepository.save(swap);
        } else if ("down".equals(direction) && currentIndex < allChoices.size() - 1) {
            ApplicantChoice swap = allChoices.get(currentIndex + 1);
            int tempPriority = choice.getPriority();
            choice.setPriority(swap.getPriority());
            swap.setPriority(tempPriority);
            choiceRepository.save(choice);
            choiceRepository.save(swap);
        } else {
            throw new BusinessException("Cannot move choice in that direction");
        }

        return choiceRepository.findById(choiceId).orElse(choice);
    }

    @Transactional
    public ApplicantChoiceSubmission submitChoices(Long submissionId) {
        ApplicantChoiceSubmission submission = findSubmissionById(submissionId);

        if ("LOCKED".equals(submission.getStatus()) || "SUBMITTED".equals(submission.getStatus())) {
            throw new BusinessException("Submission is already " + submission.getStatus());
        }

        List<ApplicantChoice> choices = choiceRepository.findBySubmission_IdOrderByPriorityAsc(submissionId);
        if (choices.isEmpty()) {
            throw new BusinessException("Please add at least one choice before submitting");
        }

        ChoiceFillingConfig config = submission.getConfig();
        if (choices.size() < config.getMinChoices()) {
            throw new BusinessException("Minimum " + config.getMinChoices() + " choices required. You have " + choices.size());
        }

        submission.setStatus("SUBMITTED");
        submission.setTotalChoices(choices.size());
        submission.setSubmittedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }

    @Transactional
    public ApplicantChoiceSubmission lockSubmission(Long submissionId) {
        ApplicantChoiceSubmission submission = findSubmissionById(submissionId);
        submission.setStatus("LOCKED");
        submission.setLockedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }

    @Transactional
    public ApplicantChoiceSubmission reopenSubmission(Long submissionId) {
        ApplicantChoiceSubmission submission = findSubmissionById(submissionId);
        if (!"LOCKED".equals(submission.getStatus()) && !"SUBMITTED".equals(submission.getStatus())) {
            throw new BusinessException("Only SUBMITTED or LOCKED submissions can be reopened");
        }
        submission.setStatus("DRAFT");
        submission.setSubmittedAt(null);
        submission.setLockedAt(null);
        return submissionRepository.save(submission);
    }

    public Map<String, Object> getStats(Long configId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", submissionRepository.countByConfig_Id(configId));
        stats.put("draft", submissionRepository.countByConfig_IdAndStatus(configId, "DRAFT"));
        stats.put("submitted", submissionRepository.countByConfig_IdAndStatus(configId, "SUBMITTED"));
        stats.put("locked", submissionRepository.countByConfig_IdAndStatus(configId, "LOCKED"));
        return stats;
    }

    public List<ApplicantChoiceSubmission> findAllByConfigId(Long configId) {
        return submissionRepository.findByConfig_Id(configId);
    }

    public List<Map<String, Object>> getDemandReport(Long configId) {
        List<Program> programs = programRepository.findByIsActive(true);
        List<String> submittedStatuses = Arrays.asList("SUBMITTED", "LOCKED");
        List<Map<String, Object>> report = new ArrayList<>();

        for (Program program : programs) {
            long demand = choiceRepository.countByProgramAndConfigAndStatuses(program.getId(), configId, submittedStatuses);
            Map<String, Object> row = new HashMap<>();
            row.put("programId", program.getId());
            row.put("programName", program.getName());
            row.put("programCode", program.getCode());
            if (program.getDepartment() != null) {
                row.put("departmentName", program.getDepartment().getName());
                if (program.getDepartment().getFaculty() != null) {
                    row.put("facultyName", program.getDepartment().getFaculty().getName());
                }
            }
            row.put("totalDemand", demand);
            report.add(row);
        }

        report.sort((a, b) -> Long.compare((long) b.get("totalDemand"), (long) a.get("totalDemand")));
        return report;
    }

    private String generateSubmissionId(PreAdmissionRegistration registration) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "SUB-" + registration.getRegistrationNumber() + "-" + timestamp;
    }

    private boolean isWindowOpen(ChoiceFillingConfig config) {
        LocalDateTime now = LocalDateTime.now();
        return "ACTIVE".equals(config.getStatus())
                && now.isAfter(config.getChoiceStartDate())
                && now.isBefore(config.getChoiceEndDate());
    }
}
