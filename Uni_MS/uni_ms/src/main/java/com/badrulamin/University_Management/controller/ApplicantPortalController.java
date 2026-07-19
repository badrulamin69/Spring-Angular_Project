package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.entity.AdmissionMeritListEntry;
import com.badrulamin.University_Management.entity.AdmissionWaitingListEntry;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.*;
import com.badrulamin.University_Management.repository.AdmissionMeritListEntryRepository;
import com.badrulamin.University_Management.repository.AdmissionWaitingListEntryRepository;
import com.badrulamin.University_Management.service.AdmissionTestAttemptService;
import com.badrulamin.University_Management.service.AdmitCardPdfService;
import com.badrulamin.University_Management.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/applicant")
@RequiredArgsConstructor
public class ApplicantPortalController {

    private final UserRepository userRepository;
    private final PreAdmissionRegistrationRepository registrationRepository;
    private final AdmissionTestAttemptService attemptService;
    private final AdmissionTestRepository testRepository;
    private final AdmissionTestResultRepository testResultRepository;
    private final DepartmentAllocationRepository allocationRepository;
    private final AdmissionTestQuestionRepository questionRepository;
    private final EnrollmentService enrollmentService;
    private final AdmitCardPdfService admitCardPdfService;
    private final AdmitCardRepository admitCardRepository;
    private final AdmissionMeritListEntryRepository meritListEntryRepository;
    private final AdmissionWaitingListEntryRepository waitingListEntryRepository;

    @GetMapping("/my-registration")
    public ResponseEntity<?> myRegistration(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", userDetails.getUsername()));
        PreAdmissionRegistration reg = registrationRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "email", user.getEmail()));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", reg.getId());
        result.put("registrationNumber", reg.getRegistrationNumber());
        result.put("firstName", reg.getFirstName());
        result.put("lastName", reg.getLastName());
        result.put("email", reg.getEmail());
        result.put("status", reg.getStatus());
        result.put("dateOfBirth", reg.getDateOfBirth());
        result.put("gender", reg.getGender());
        result.put("sscGpa", reg.getSscGpa());
        result.put("hscGpa", reg.getHscGpa());
        result.put("programPreference1", reg.getProgramPreference1());
        result.put("programPreference2", reg.getProgramPreference2());
        result.put("programPreference3", reg.getProgramPreference3());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-test")
    public ResponseEntity<?> myTest(@AuthenticationPrincipal UserDetails userDetails) {
        PreAdmissionRegistration reg = findRegistration(userDetails);
        List<AdmissionTest> tests = testRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 10)).getContent();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("registrationId", reg.getId());
        result.put("registrationStatus", reg.getStatus());
        if (!tests.isEmpty()) {
            AdmissionTest test = tests.get(0);
            result.put("testId", test.getId());
            result.put("testName", test.getName());
            result.put("testDate", test.getTestDate());
            result.put("totalMarks", test.getTotalMarks());
            result.put("passingMarks", test.getPassingMarks());
            result.put("description", test.getDescription());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-test/{testId}/questions")
    public ResponseEntity<?> getTestQuestions(@PathVariable Long testId, @AuthenticationPrincipal UserDetails userDetails) {
        PreAdmissionRegistration reg = findRegistration(userDetails);
        List<AdmissionTestQuestion> questions = questionRepository.findByTest_IdOrderByCreatedAtAsc(testId);
        List<Map<String, Object>> safeQuestions = new ArrayList<>();
        for (AdmissionTestQuestion q : questions) {
            Map<String, Object> qMap = new LinkedHashMap<>();
            qMap.put("id", q.getId());
            qMap.put("questionText", q.getQuestionText());
            qMap.put("optionA", q.getOptionA());
            qMap.put("optionB", q.getOptionB());
            qMap.put("optionC", q.getOptionC());
            qMap.put("optionD", q.getOptionD());
            qMap.put("marks", q.getMarks());
            safeQuestions.add(qMap);
        }
        return ResponseEntity.ok(safeQuestions);
    }

    @PostMapping("/test/{testId}/start")
    public ResponseEntity<?> startTest(@PathVariable Long testId, @AuthenticationPrincipal UserDetails userDetails) {
        PreAdmissionRegistration reg = findRegistration(userDetails);
        AdmissionTestAttempt attempt = attemptService.startTest(reg.getId(), testId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("attemptId", attempt.getId());
        result.put("status", attempt.getStatus());
        result.put("totalQuestions", attempt.getTotalQuestions());
        result.put("startedAt", attempt.getStartedAt());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/test/submit")
    public ResponseEntity<?> submitTest(@RequestBody Map<String, Object> body, @AuthenticationPrincipal UserDetails userDetails) {
        Long attemptId = Long.parseLong(body.get("attemptId").toString());
        @SuppressWarnings("unchecked")
        Map<String, String> answers = (Map<String, String>) body.get("answers");
        AdmissionTestAttempt attempt = attemptService.submitTest(attemptId, answers);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("attemptId", attempt.getId());
        result.put("score", attempt.getScore());
        result.put("maxScore", attempt.getMaxScore());
        result.put("percentage", attempt.getPercentage());
        result.put("correctAnswers", attempt.getCorrectAnswers());
        result.put("totalQuestions", attempt.getTotalQuestions());
        result.put("status", attempt.getStatus());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-results")
    public ResponseEntity<?> myResults(@AuthenticationPrincipal UserDetails userDetails) {
        PreAdmissionRegistration reg = findRegistration(userDetails);
        List<AdmissionTestAttempt> attempts = attemptService.findByRegistrationId(reg.getId());
        List<AdmissionTestResult> results = testResultRepository.findByRegistration_Id(reg.getId()).map(List::of).orElse(List.of());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("registrationNumber", reg.getRegistrationNumber());
        result.put("registrationStatus", reg.getStatus());
        result.put("attempts", attempts);
        result.put("testResults", results);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-allocation")
    public ResponseEntity<?> myAllocation(@AuthenticationPrincipal UserDetails userDetails) {
        PreAdmissionRegistration reg = findRegistration(userDetails);
        Optional<DepartmentAllocation> allocation = allocationRepository.findByRegistration_Id(reg.getId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("registrationNumber", reg.getRegistrationNumber());
        result.put("registrationStatus", reg.getStatus());
        if (allocation.isPresent()) {
            DepartmentAllocation a = allocation.get();
            result.put("allocationId", a.getId());
            result.put("allocationNumber", a.getAllocationNumber());
            result.put("totalScore", a.getTotalScore());
            result.put("meritRank", a.getMeritRank());
            result.put("status", a.getStatus());
            result.put("allocatedProgram", a.getAllocatedProgram());
            result.put("allocatedDepartment", a.getAllocatedDepartment());
            result.put("allocatedBatch", a.getAllocatedBatch());
            result.put("allocatedSection", a.getAllocatedSection());
            result.put("allocatedAt", a.getAllocatedAt());
            result.put("confirmedAt", a.getConfirmedAt());
            result.put("isEnrolled", "ENROLLED".equals(reg.getStatus()));
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/my-allocation/{id}/confirm")
    public ResponseEntity<?> confirmAllocation(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        PreAdmissionRegistration reg = findRegistration(userDetails);
        DepartmentAllocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartmentAllocation", "id", id));
        if (!allocation.getRegistration().getId().equals(reg.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "This allocation does not belong to you"));
        }
        allocation.setStatus("CONFIRMED");
        allocation.setConfirmedAt(java.time.LocalDateTime.now());
        allocationRepository.save(allocation);
        reg.setStatus("ALLOCATED");
        registrationRepository.save(reg);
        return ResponseEntity.ok(Map.of("message", "Allocation confirmed successfully", "status", "CONFIRMED"));
    }

    @PostMapping("/my-allocation/{id}/decline")
    public ResponseEntity<?> declineAllocation(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        PreAdmissionRegistration reg = findRegistration(userDetails);
        DepartmentAllocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartmentAllocation", "id", id));
        if (!allocation.getRegistration().getId().equals(reg.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "This allocation does not belong to you"));
        }
        allocation.setStatus("CANCELLED");
        allocationRepository.save(allocation);
        return ResponseEntity.ok(Map.of("message", "Allocation declined", "status", "CANCELLED"));
    }

    @PostMapping("/my-enroll")
    public ResponseEntity<?> enrollSelf(@AuthenticationPrincipal UserDetails userDetails) {
        PreAdmissionRegistration reg = findRegistration(userDetails);
        try {
            Map<String, Object> result = enrollmentService.enrollSelf(reg.getId(), reg.getEmail());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-admit-card/pdf")
    public ResponseEntity<byte[]> downloadAdmitCardPdf(@AuthenticationPrincipal UserDetails userDetails) {
        PreAdmissionRegistration reg = findRegistration(userDetails);
        AdmitCard admitCard = admitCardRepository.findByRegistrationId(reg.getId()).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("AdmitCard", "registrationId", reg.getId()));
        byte[] pdf = admitCardPdfService.generateAdmitCardPdf(admitCard);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=admit-card-" + reg.getRegistrationNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    private PreAdmissionRegistration findRegistration(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", userDetails.getUsername()));
        List<PreAdmissionRegistration> all = registrationRepository.findAll();
        return all.stream().filter(r -> r.getEmail().equals(user.getEmail())).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "email", user.getEmail()));
    }

    @GetMapping("/my-merit")
    public ResponseEntity<?> getMyMeritPosition(@AuthenticationPrincipal UserDetails userDetails) {
        PreAdmissionRegistration reg = findRegistration(userDetails);
        List<AdmissionMeritListEntry> entries = meritListEntryRepository.findByRegistration_Id(reg.getId());
        if (entries.isEmpty()) {
            return ResponseEntity.ok(Map.of("found", false, "message", "No merit list entry found"));
        }
        AdmissionMeritListEntry bestEntry = entries.stream()
                .filter(e -> "SELECTED".equals(e.getStatus()) || "WAITING".equals(e.getStatus()))
                .min((a, b) -> Integer.compare(a.getRank(), b.getRank()))
                .orElse(entries.get(0));
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("found", true);
        result.put("rank", bestEntry.getRank());
        result.put("score", bestEntry.getTotalWeightedScore());
        result.put("testMarks", bestEntry.getTestMarks());
        result.put("status", bestEntry.getStatus());
        result.put("meritListName", bestEntry.getMeritList().getName());
        result.put("meritListId", bestEntry.getMeritList().getId());
        result.put("listStatus", bestEntry.getMeritList().getStatus());
        result.put("applicantName", bestEntry.getApplicantName());
        result.put("rollNumber", bestEntry.getRollNumber());
        result.put("programName", bestEntry.getProgramName());
        result.put("totalApplicants", bestEntry.getMeritList().getTotalApplicants());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-waiting-position")
    public ResponseEntity<?> getMyWaitingPosition(@AuthenticationPrincipal UserDetails userDetails) {
        PreAdmissionRegistration reg = findRegistration(userDetails);
        List<AdmissionWaitingListEntry> entries = waitingListEntryRepository.findByRegistration_Id(reg.getId());
        if (entries.isEmpty()) {
            return ResponseEntity.ok(Map.of("found", false, "message", "No waiting list entry found"));
        }
        AdmissionWaitingListEntry bestEntry = entries.stream()
                .min((a, b) -> Integer.compare(a.getRank(), b.getRank()))
                .orElse(entries.get(0));
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("found", true);
        result.put("rank", bestEntry.getRank());
        result.put("score", bestEntry.getTotalWeightedScore());
        result.put("status", bestEntry.getStatus());
        result.put("waitingListName", bestEntry.getWaitingList().getName());
        result.put("applicantName", bestEntry.getApplicantName());
        result.put("rollNumber", bestEntry.getRollNumber());
        return ResponseEntity.ok(result);
    }
}
