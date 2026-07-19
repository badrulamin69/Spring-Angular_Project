package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.service.AdmissionTestReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admission-test-reports")
@RequiredArgsConstructor
public class AdmissionTestReportController {

    private final AdmissionTestReportService reportService;

    @GetMapping("/summary/{testId}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<Map<String, Object>> getTestSummary(@PathVariable Long testId) {
        return ResponseEntity.ok(reportService.getTestSummary(testId));
    }

    @GetMapping("/eligibility/{testId}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<Map<String, Object>> getEligibilityReport(@PathVariable Long testId) {
        return ResponseEntity.ok(reportService.getEligibilityReport(testId));
    }

    @GetMapping("/attendance/{testId}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<Map<String, Object>> getAttendanceReport(@PathVariable Long testId) {
        return ResponseEntity.ok(reportService.getAttendanceReport(testId));
    }
}
