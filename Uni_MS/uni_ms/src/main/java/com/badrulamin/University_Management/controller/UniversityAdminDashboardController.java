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
@RequestMapping("/api/dashboards/university-admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_UNIVERSITY_ADMIN')")
public class UniversityAdminDashboardController {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EmployeeRepository employeeRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalFaculties", facultyRepository.count());
        stats.put("totalDepartments", departmentRepository.count());
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalEmployees", employeeRepository.count());
        stats.put("pendingApprovals", 0);
        stats.put("recentActivities", 0);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
