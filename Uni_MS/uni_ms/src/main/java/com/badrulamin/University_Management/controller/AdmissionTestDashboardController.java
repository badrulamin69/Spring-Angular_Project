package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.service.AdmissionTestDashboardService;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admission-test-dashboard")
@RequiredArgsConstructor
public class AdmissionTestDashboardController {

    private final AdmissionTestDashboardService admissionTestDashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(admissionTestDashboardService.getStats()));
    }

    @GetMapping("/charts")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getChartData() {
        return ResponseEntity.ok(ApiResponse.success(admissionTestDashboardService.getChartData()));
    }
}
