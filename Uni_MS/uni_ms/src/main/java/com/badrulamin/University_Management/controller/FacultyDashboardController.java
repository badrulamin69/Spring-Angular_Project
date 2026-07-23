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
@RequestMapping("/api/dashboards/faculty")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_FACULTY')")
public class FacultyDashboardController {

    private final CourseAssignmentRepository courseAssignmentRepository;
    private final StudentRepository studentRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("assignedCourses", courseAssignmentRepository.count());
        stats.put("totalStudents", studentRepository.count());
        stats.put("pendingAssignments", assignmentRepository.count());
        stats.put("upcomingExams", examScheduleRepository.count());
        stats.put("recentSubmissions", assignmentSubmissionRepository.count());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
