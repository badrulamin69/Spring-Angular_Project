package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.payload.request.AdminEnrollmentActionRequest;
import com.badrulamin.University_Management.payload.request.EnrollmentApprovalRequest;
import com.badrulamin.University_Management.payload.request.SemesterEnrollmentRequest;
import com.badrulamin.University_Management.payload.response.*;
import com.badrulamin.University_Management.repository.UserRepository;
import com.badrulamin.University_Management.service.EnrollmentHistoryService;
import com.badrulamin.University_Management.service.SemesterEnrollmentService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/semester-enrollments")
@RequiredArgsConstructor
public class SemesterEnrollmentController {

    private final SemesterEnrollmentService semesterEnrollmentService;
    private final EnrollmentHistoryService enrollmentHistoryService;
    private final UserRepository userRepository;

    @GetMapping("/eligibility/{studentId}/semester/{semesterId}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<EnrollmentEligibilityResponse>> checkEligibility(
            @PathVariable Long studentId, @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(semesterEnrollmentService.checkEligibility(studentId, semesterId)));
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_MANAGE')")
    public ResponseEntity<ApiResponse<SemesterEnrollmentResponse>> enroll(
            @Valid @RequestBody SemesterEnrollmentRequest request,
            Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        return ResponseEntity.ok(ApiResponse.success("Enrollment successful",
                semesterEnrollmentService.enroll(request, user != null ? user.getId() : null)));
    }

    @PostMapping("/force-enroll")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_MANAGE')")
    public ResponseEntity<ApiResponse<SemesterEnrollmentResponse>> forceEnroll(
            @Valid @RequestBody SemesterEnrollmentRequest request,
            Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        return ResponseEntity.ok(ApiResponse.success("Force enrollment successful",
                semesterEnrollmentService.forceEnroll(request, user != null ? user.getId() : null)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<SemesterEnrollmentResponse>> getEnrollmentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(semesterEnrollmentService.getEnrollmentById(id)));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<List<SemesterEnrollmentResponse>>> getStudentEnrollments(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(semesterEnrollmentService.getStudentEnrollments(studentId)));
    }

    @GetMapping("/student/{studentId}/semester/{semesterId}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<SemesterEnrollmentResponse>> getStudentEnrollmentForSemester(
            @PathVariable Long studentId, @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(
                semesterEnrollmentService.getStudentEnrollmentForSemester(studentId, semesterId)));
    }

    @GetMapping("/pending/semester/{semesterId}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_APPROVE')")
    public ResponseEntity<ApiResponse<List<SemesterEnrollmentResponse>>> getPendingApprovals(
            @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(semesterEnrollmentService.getPendingApprovals(semesterId)));
    }

    @GetMapping("/pending/advisor/{advisorId}/semester/{semesterId}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_APPROVE')")
    public ResponseEntity<ApiResponse<List<SemesterEnrollmentResponse>>> getPendingApprovalsForAdvisor(
            @PathVariable Long advisorId, @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(
                semesterEnrollmentService.getPendingApprovalsForAdvisor(advisorId, semesterId)));
    }

    @PostMapping("/approval")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_APPROVE')")
    public ResponseEntity<ApiResponse<EnrollmentApprovalResponse>> processApproval(
            @Valid @RequestBody EnrollmentApprovalRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        String ipAddress = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(ApiResponse.success("Approval processed successfully",
                semesterEnrollmentService.processApproval(request, user != null ? user.getId() : null, ipAddress)));
    }

    @PostMapping("/{enrollmentId}/cancel")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_MANAGE')")
    public ResponseEntity<ApiResponse<SemesterEnrollmentResponse>> cancelEnrollment(
            @PathVariable Long enrollmentId,
            @Valid @RequestBody AdminEnrollmentActionRequest request,
            Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        return ResponseEntity.ok(ApiResponse.success("Enrollment cancelled",
                semesterEnrollmentService.cancelEnrollment(enrollmentId,
                        user != null ? user.getId() : null, request.getReason())));
    }

    @PostMapping("/{enrollmentId}/reopen")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_MANAGE')")
    public ResponseEntity<ApiResponse<SemesterEnrollmentResponse>> reopenEnrollment(
            @PathVariable Long enrollmentId,
            Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        return ResponseEntity.ok(ApiResponse.success("Enrollment reopened",
                semesterEnrollmentService.reopenEnrollment(enrollmentId, user != null ? user.getId() : null)));
    }

    @PostMapping("/{enrollmentId}/finalize")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_MANAGE')")
    public ResponseEntity<ApiResponse<SemesterEnrollmentResponse>> finalizeEnrollment(
            @PathVariable Long enrollmentId,
            Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        return ResponseEntity.ok(ApiResponse.success("Enrollment finalized",
                semesterEnrollmentService.finalizeEnrollment(enrollmentId, user != null ? user.getId() : null)));
    }

    @GetMapping("/dashboard/{semesterId}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<EnrollmentDashboardResponse>> getDashboardStats(
            @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(semesterEnrollmentService.getDashboardStats(semesterId)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<?> getAllEnrollments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) String status) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<SemesterEnrollmentResponse> items = semesterEnrollmentService.getFilteredEnrollments(
                semesterId, departmentId, facultyId, programId, status, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", items.getContent());
        response.put("totalElements", items.getTotalElements());
        response.put("totalPages", items.getTotalPages());
        response.put("currentPage", items.getNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/student/{studentId}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<?> getHistoryByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(enrollmentHistoryService.getHistoryByStudent(studentId, PageRequest.of(page, size)));
    }

    @GetMapping("/history/semester/{semesterId}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<?>> getHistoryBySemester(@PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentHistoryService.getHistoryBySemester(semesterId)));
    }

    @GetMapping("/history/enrollment/{enrollmentId}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<?>> getHistoryByEnrollment(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentHistoryService.getHistoryByEnrollment(enrollmentId)));
    }
}
