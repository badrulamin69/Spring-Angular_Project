package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.ApplicantChoice;
import com.badrulamin.University_Management.entity.ApplicantChoiceSubmission;
import com.badrulamin.University_Management.entity.ChoiceFillingConfig;
import com.badrulamin.University_Management.entity.PreAdmissionRegistration;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.repository.ChoiceFillingConfigRepository;
import com.badrulamin.University_Management.repository.PreAdmissionRegistrationRepository;
import com.badrulamin.University_Management.repository.UserRepository;
import com.badrulamin.University_Management.service.ApplicantChoiceService;
import com.badrulamin.University_Management.service.ChoiceSubmissionPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applicant-choices")
@RequiredArgsConstructor
public class ApplicantChoiceController {

    private final ApplicantChoiceService choiceService;
    private final UserRepository userRepository;
    private final PreAdmissionRegistrationRepository registrationRepository;
    private final ChoiceFillingConfigRepository configRepository;
    private final ChoiceSubmissionPdfService pdfService;

    @GetMapping("/admin/submissions")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<PagedResponse<ApplicantChoiceSubmission>> getAllSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long configId) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ApplicantChoiceSubmission> paged = choiceService.findSubmissionsByFilters(search, status, configId, pageable);
        return ResponseEntity.ok(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast()));
    }

    @GetMapping("/admin/submissions/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApplicantChoiceSubmission> getSubmissionById(@PathVariable Long id) {
        return ResponseEntity.ok(choiceService.findSubmissionById(id));
    }

    @GetMapping("/admin/submissions/{id}/choices")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<List<ApplicantChoice>> getSubmissionChoices(@PathVariable Long id) {
        return ResponseEntity.ok(choiceService.getChoicesBySubmission(id));
    }

    @PutMapping("/admin/submissions/{id}/lock")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApplicantChoiceSubmission> lockSubmission(@PathVariable Long id) {
        return ResponseEntity.ok(choiceService.lockSubmission(id));
    }

    @PutMapping("/admin/submissions/{id}/reopen")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApplicantChoiceSubmission> reopenSubmission(@PathVariable Long id) {
        return ResponseEntity.ok(choiceService.reopenSubmission(id));
    }

    @GetMapping("/admin/stats/{configId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable Long configId) {
        return ResponseEntity.ok(choiceService.getStats(configId));
    }

    @GetMapping("/admin/available-programs/{configId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<List<Map<String, Object>>> getAvailablePrograms(@PathVariable Long configId) {
        return ResponseEntity.ok(choiceService.getAvailablePrograms(configId));
    }

    @GetMapping("/my-submission")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApplicantChoiceSubmission> getMySubmission(
            @RequestParam Long configId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(choiceService.findSubmissionByRegAndConfig(
                getUserIdFromUsername(userDetails.getUsername()), configId));
    }

    @GetMapping("/my-choices")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<List<ApplicantChoice>> getMyChoices(
            @RequestParam Long submissionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(choiceService.getChoicesBySubmission(submissionId));
    }

    @PostMapping("/start/{configId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApplicantChoiceSubmission> startSubmission(
            @PathVariable Long configId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long registrationId = getUserIdFromUsername(userDetails.getUsername());
        return ResponseEntity.ok(choiceService.getOrCreateSubmission(registrationId, configId));
    }

    @PostMapping("/add-choice/{submissionId}/{programId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApplicantChoice> addChoice(@PathVariable Long submissionId, @PathVariable Long programId) {
        return ResponseEntity.ok(choiceService.addChoice(submissionId, programId));
    }

    @DeleteMapping("/remove-choice/{choiceId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<Void> removeChoice(@PathVariable Long choiceId) {
        choiceService.removeChoice(choiceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/move-choice/{choiceId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApplicantChoice> moveChoice(
            @PathVariable Long choiceId,
            @RequestParam String direction) {
        return ResponseEntity.ok(choiceService.moveChoice(choiceId, direction));
    }

    @PostMapping("/submit/{submissionId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApplicantChoiceSubmission> submitChoices(@PathVariable Long submissionId) {
        return ResponseEntity.ok(choiceService.submitChoices(submissionId));
    }

    @GetMapping("/admin/submissions/export/pdf")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<byte[]> exportSubmissionsPdf(@RequestParam Long configId) {
        ChoiceFillingConfig config = configRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("ChoiceFillingConfig", "id", configId));
        List<ApplicantChoiceSubmission> submissions = choiceService.findAllByConfigId(configId);
        byte[] pdf = pdfService.generateAdminSubmissionsReport(submissions, config);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=choice-submissions-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    @GetMapping("/my-choices/pdf")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<byte[]> exportMyChoicesPdf(
            @RequestParam Long submissionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        ApplicantChoiceSubmission submission = choiceService.findSubmissionById(submissionId);
        List<ApplicantChoice> choices = choiceService.getChoicesBySubmission(submissionId);
        byte[] pdf = pdfService.generateApplicantChoicesPdf(submission, choices);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=my-choices.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    @GetMapping("/admin/demand-report/{configId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<List<Map<String, Object>>> getDemandReport(@PathVariable Long configId) {
        return ResponseEntity.ok(choiceService.getDemandReport(configId));
    }

    private Long getUserIdFromUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        PreAdmissionRegistration registration = registrationRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "email", user.getEmail()));
        return registration.getId();
    }
}
