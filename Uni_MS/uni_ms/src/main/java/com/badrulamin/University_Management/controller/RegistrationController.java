package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.CourseRegistration;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.payload.request.CourseRegistrationRequest;
import com.badrulamin.University_Management.payload.request.PaymentValidationRequest;
import com.badrulamin.University_Management.payload.response.*;
import com.badrulamin.University_Management.repository.UserRepository;
import com.badrulamin.University_Management.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final EligibilityService eligibilityService;
    private final ValidationService validationService;
    private final RegistrationHistoryService historyService;
    private final UserRepository userRepository;

    @GetMapping("/student/{studentId}/semester/{semesterId}")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<List<CourseRegistration>>> getStudentRegistrations(
            @PathVariable Long studentId, @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getStudentRegistrations(studentId, semesterId)));
    }

    @GetMapping("/summary/{studentId}/semester/{semesterId}")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<RegistrationSummaryResponse>> getRegistrationSummary(
            @PathVariable Long studentId, @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getRegistrationSummary(studentId, semesterId)));
    }

    @PostMapping("/select")
    @PreAuthorize("hasAuthority('REGISTRATION_MANAGE')")
    public ResponseEntity<ApiResponse<CourseRegistration>> selectCourse(
            @Valid @RequestBody CourseRegistrationRequest request,
            Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        return ResponseEntity.ok(ApiResponse.success("Course selected successfully", registrationService.selectCourse(request, user != null ? user.getId() : null)));
    }

    @PostMapping("/drop/{registrationId}")
    @PreAuthorize("hasAuthority('REGISTRATION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> dropCourse(
            @PathVariable Long registrationId,
            Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        registrationService.dropCourse(registrationId, user != null ? user.getId() : null);
        return ResponseEntity.ok(ApiResponse.success("Course dropped successfully", null));
    }

    @PostMapping("/finalize/{registrationId}")
    @PreAuthorize("hasAuthority('REGISTRATION_MANAGE')")
    public ResponseEntity<ApiResponse<CourseRegistration>> finalizeRegistration(
            @PathVariable Long registrationId,
            Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        return ResponseEntity.ok(ApiResponse.success("Registration finalized", registrationService.finalizeRegistration(registrationId, user != null ? user.getId() : null)));
    }

    @PostMapping("/payment")
    @PreAuthorize("hasAuthority('REGISTRATION_MANAGE')")
    public ResponseEntity<ApiResponse<AdvisorApprovalResponse>> processPayment(
            @Valid @RequestBody PaymentValidationRequest request,
            Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", registrationService.processPaymentValidation(request, user != null ? user.getId() : null)));
    }

    @GetMapping("/dashboard/{semesterId}")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<RegistrationDashboardResponse>> getDashboardStats(@PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getDashboardStats(semesterId)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long semesterId) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<CourseRegistration> items = registrationService.getAllRegistrations(
                PageRequest.of(page, size, Sort.by(sortDirection, sort)), status, semesterId);
        Map<String, Object> response = new HashMap<>();
        response.put("content", items.getContent());
        response.put("totalElements", items.getTotalElements());
        response.put("totalPages", items.getTotalPages());
        response.put("currentPage", items.getNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<CourseRegistration>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getRegistrationById(id)));
    }

    @GetMapping("/eligibility/{studentId}/semester/{semesterId}")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<EligibilityCheckResponse>> checkEligibility(
            @PathVariable Long studentId, @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(eligibilityService.checkEligibility(studentId, semesterId)));
    }

    @GetMapping("/validate/{studentId}/subject/{subjectId}/semester/{semesterId}")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateRegistration(
            @PathVariable Long studentId,
            @PathVariable Long subjectId,
            @PathVariable Long semesterId,
            @RequestParam(required = false) Long batchId) {
        List<String> errors = validationService.validateRegistration(studentId, subjectId, semesterId, batchId, null);
        Map<String, Object> result = new HashMap<>();
        result.put("valid", errors.isEmpty());
        result.put("errors", errors);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/history/student/{studentId}")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<?> getHistoryByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(historyService.getHistoryByStudent(studentId, PageRequest.of(page, size)));
    }

    @GetMapping("/history/semester/{semesterId}")
    @PreAuthorize("hasAuthority('REGISTRATION_VIEW')")
    public ResponseEntity<ApiResponse<?>> getHistoryBySemester(@PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(historyService.getHistoryBySemester(semesterId)));
    }
}
