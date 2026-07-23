package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.repository.*;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboards/admission-officer")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMISSION_OFFICER')")
public class AdmissionDashboardController_Role {

    private final AdmissionApplicationRepository admissionApplicationRepository;
    private final DocumentVerificationRepository documentVerificationRepository;
    private final AdmissionEnrollmentRepository admissionEnrollmentRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalApplicants", admissionApplicationRepository.count());
        stats.put("pendingVerifications", documentVerificationRepository.count());
        stats.put("totalEnrollments", admissionEnrollmentRepository.count());
        stats.put("recentApplications", admissionApplicationRepository.count());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
