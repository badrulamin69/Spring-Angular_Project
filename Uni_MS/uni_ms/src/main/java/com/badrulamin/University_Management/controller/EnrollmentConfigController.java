package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.payload.request.EnrollmentConfigRequest;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.EnrollmentConfigResponse;
import com.badrulamin.University_Management.service.EnrollmentConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment-configs")
@RequiredArgsConstructor
public class EnrollmentConfigController {

    private final EnrollmentConfigService enrollmentConfigService;

    @GetMapping
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<List<EnrollmentConfigResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(enrollmentConfigService.findAll()));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<List<EnrollmentConfigResponse>>> findActive() {
        return ResponseEntity.ok(ApiResponse.success(enrollmentConfigService.findActive()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<EnrollmentConfigResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentConfigService.findById(id)));
    }

    @GetMapping("/semester/{semesterId}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<EnrollmentConfigResponse>> findBySemester(@PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentConfigService.findBySemester(semesterId)));
    }

    @GetMapping("/check/{semesterId}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_VIEW')")
    public ResponseEntity<ApiResponse<Boolean>> isEnrollmentOpen(@PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentConfigService.isEnrollmentOpen(semesterId)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_MANAGE')")
    public ResponseEntity<ApiResponse<EnrollmentConfigResponse>> create(@Valid @RequestBody EnrollmentConfigRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Enrollment config created successfully", enrollmentConfigService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_MANAGE')")
    public ResponseEntity<ApiResponse<EnrollmentConfigResponse>> update(@PathVariable Long id, @Valid @RequestBody EnrollmentConfigRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Enrollment config updated successfully", enrollmentConfigService.update(id, request)));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_MANAGE')")
    public ResponseEntity<ApiResponse<EnrollmentConfigResponse>> closeEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Enrollment closed successfully", enrollmentConfigService.closeEnrollment(id)));
    }

    @PostMapping("/{id}/reopen")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_MANAGE')")
    public ResponseEntity<ApiResponse<EnrollmentConfigResponse>> reopenEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Enrollment reopened successfully", enrollmentConfigService.reopenEnrollment(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SEMESTER_ENROLLMENT_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        enrollmentConfigService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Enrollment config deleted successfully", null));
    }
}
