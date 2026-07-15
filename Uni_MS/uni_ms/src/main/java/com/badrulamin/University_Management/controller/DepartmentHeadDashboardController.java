package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboards/department-head")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_DEPT_HEAD')")
public class DepartmentHeadDashboardController {

    private final EmployeeRepository employeeRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalFacultyMembers", employeeRepository.count());
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalStudents", studentRepository.count());
        stats.put("pendingApprovals", 0);
        stats.put("departmentPerformance", "N/A");
        return ResponseEntity.ok(stats);
    }
}
