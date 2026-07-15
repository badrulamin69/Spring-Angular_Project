package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admission-dashboard")
public class AdmissionDashboardController {

    private final AdmissionApplicationService applicationService;
    private final AdmissionCircularService circularService;
    private final AdmissionInterviewService interviewService;
    private final AdmissionEnrollmentService enrollmentService;

    public AdmissionDashboardController(
            AdmissionApplicationService applicationService,
            AdmissionCircularService circularService,
            AdmissionInterviewService interviewService,
            AdmissionEnrollmentService enrollmentService) {
        this.applicationService = applicationService;
        this.circularService = circularService;
        this.interviewService = interviewService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalApplications", applicationService.countByStatus("SUBMITTED") + applicationService.countByStatus("APPROVED") + applicationService.countByStatus("REJECTED"));
        stats.put("pendingInterviews", interviewService.countByStatus("SCHEDULED"));
        stats.put("publishedCirculars", circularService.countByStatus("PUBLISHED"));
        stats.put("pendingEnrollments", enrollmentService.countByStatus("PENDING"));
        stats.put("totalEnrolled", enrollmentService.countByStatus("ENROLLED"));
        return ResponseEntity.ok(stats);
    }
}
