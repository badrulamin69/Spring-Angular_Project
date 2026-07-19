package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.service.AdmissionTestDashboardService;
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
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(admissionTestDashboardService.getStats());
    }

    @GetMapping("/charts")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<Map<String, Object>> getChartData() {
        return ResponseEntity.ok(admissionTestDashboardService.getChartData());
    }
}
