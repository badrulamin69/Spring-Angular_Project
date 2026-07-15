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
@RequestMapping("/api/academic-dashboard")
@RequiredArgsConstructor
public class AcademicDashboardController {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final BatchRepository batchRepository;
    private final SectionRepository sectionRepository;
    private final ProgramRepository programRepository;
    private final SemesterRepository semesterRepository;
    private final AcademicSessionRepository academicSessionRepository;
    private final CampusRepository campusRepository;
    private final AdministrationDivisionRepository administrationDivisionRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalFaculties", facultyRepository.count());
        stats.put("totalDepartments", departmentRepository.count());
        stats.put("totalPrograms", programRepository.count());
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalSubjects", subjectRepository.count());
        stats.put("totalBatches", batchRepository.count());
        stats.put("totalSections", sectionRepository.count());
        stats.put("activeSemesters", semesterRepository.count());
        stats.put("activeSessions", academicSessionRepository.count());
        stats.put("activeCampuses", campusRepository.count());
        stats.put("totalAdministrationDivisions", administrationDivisionRepository.count());
        return ResponseEntity.ok(stats);
    }
}
