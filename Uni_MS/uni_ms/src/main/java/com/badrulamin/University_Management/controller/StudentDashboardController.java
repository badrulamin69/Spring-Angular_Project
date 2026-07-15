package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboards/student")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_STUDENT')")
public class StudentDashboardController {

    private final StudentRepository studentRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final AlumniRepository alumniRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final StudentFeeRepository studentFeeRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalStudents", studentRepository.count());
        stats.put("activeEnrollments", studentEnrollmentRepository.count());
        stats.put("totalAlumni", alumniRepository.count());
        stats.put("registeredCourses", courseRegistrationRepository.count());
        stats.put("pendingAssignments", assignmentSubmissionRepository.count());
        stats.put("pendingFees", studentFeeRepository.count());
        return ResponseEntity.ok(stats);
    }
}
