package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdmissionTestDashboardService {

    private final AdmissionTestRepository testRepository;
    private final AdmissionTestQuestionRepository questionRepository;
    private final PreAdmissionRegistrationRepository registrationRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final AdmitCardRepository admitCardRepository;
    private final AdmissionAttendanceRepository attendanceRepository;
    private final AdmissionTestResultRepository testResultRepository;
    private final AdmissionTestAttemptRepository attemptRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        long totalTests = testRepository.count();
        long publishedTests = testRepository.countByStatus("PUBLISHED");
        long draftTests = testRepository.countByStatus("DRAFT");
        long closedTests = testRepository.countByStatus("CLOSED");

        long totalApplicants = registrationRepository.countByStatus("SUBMITTED") +
                registrationRepository.countByStatus("ADMIT_CARD_GENERATED") +
                registrationRepository.countByStatus("TEST_COMPLETED");

        long totalEligible = registrationRepository.countByStatus("ADMIT_CARD_GENERATED") +
                registrationRepository.countByStatus("TEST_COMPLETED") +
                registrationRepository.countByStatus("MERIT_PROCESSED");

        long admitCardsGenerated = admitCardRepository.count();
        long totalAttempts = attemptRepository.count();
        long pendingResults = attemptRepository.countByStatus("COMPLETED") - testResultRepository.count();

        long totalPresent = 0;
        long totalAbsent = 0;
        long totalLate = 0;
        for (var test : testRepository.findAll()) {
            totalPresent += attendanceRepository.countPresentByTestId(test.getId());
            totalAbsent += attendanceRepository.countAbsentByTestId(test.getId());
            totalLate += attendanceRepository.countLateByTestId(test.getId());
        }

        long passed = testResultRepository.countByStatus("PASS");
        long failed = testResultRepository.countByStatus("FAIL");
        long pending = testResultRepository.countByStatus("PENDING");

        stats.put("totalTests", totalTests);
        stats.put("publishedTests", publishedTests);
        stats.put("draftTests", draftTests);
        stats.put("closedTests", closedTests);
        stats.put("totalApplicants", totalApplicants);
        stats.put("totalEligible", totalEligible);
        stats.put("admitCardsGenerated", admitCardsGenerated);
        stats.put("totalAttempts", totalAttempts);
        stats.put("presentCandidates", totalPresent);
        stats.put("absentCandidates", totalAbsent);
        stats.put("lateCandidates", totalLate);
        stats.put("passedCandidates", passed);
        stats.put("failedCandidates", failed);
        stats.put("pendingResults", Math.max(0, pendingResults));

        return stats;
    }

    public Map<String, Object> getChartData() {
        Map<String, Object> charts = new LinkedHashMap<>();

        List<Map<String, Object>> facultyWise = new ArrayList<>();
        for (var faculty : facultyRepository.findAll()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("faculty", faculty.getName());
            item.put("count", testRepository.countByFacultyId(faculty.getId()));
            facultyWise.add(item);
        }
        charts.put("applicantsByFaculty", facultyWise);

        List<Map<String, Object>> departmentWise = new ArrayList<>();
        for (var dept : departmentRepository.findAll()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("department", dept.getName());
            item.put("count", testRepository.countByDepartmentId(dept.getId()));
            departmentWise.add(item);
        }
        charts.put("applicantsByDepartment", departmentWise);

        long totalPresent = 0;
        long totalAbsent = 0;
        long totalLate = 0;
        for (var test : testRepository.findAll()) {
            totalPresent += attendanceRepository.countPresentByTestId(test.getId());
            totalAbsent += attendanceRepository.countAbsentByTestId(test.getId());
            totalLate += attendanceRepository.countLateByTestId(test.getId());
        }
        charts.put("attendanceStats", Map.of(
                "present", totalPresent,
                "absent", totalAbsent,
                "late", totalLate
        ));

        long passed = testResultRepository.countByStatus("PASS");
        long failed = testResultRepository.countByStatus("FAIL");
        charts.put("passRate", Map.of(
                "passed", passed,
                "failed", failed,
                "total", passed + failed
        ));

        return charts;
    }
}
