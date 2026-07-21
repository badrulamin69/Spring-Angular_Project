package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionTest;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdmissionTestReportService {

    private final AdmissionTestRepository testRepository;
    private final AdmissionTestAttemptRepository attemptRepository;
    private final AdmissionTestQuestionRepository questionRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final AdmitCardRepository admitCardRepository;

    public Map<String, Object> getTestSummary(Long testId) {
        AdmissionTest test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTest", "id", testId));

        Map<String, Object> summary = new HashMap<>();
        summary.put("testId", test.getId());
        summary.put("testName", test.getName());
        summary.put("testDate", test.getTestDate());
        summary.put("totalMarks", test.getTotalMarks());
        summary.put("passingMarks", test.getPassingMarks());
        summary.put("totalQuestions", questionRepository.countByTest_Id(testId));
        summary.put("totalAttempts", attemptRepository.countByTest_Id(testId));
        summary.put("gradedAttempts", attemptRepository.countByTest_IdAndStatus(testId, "GRADED"));
        summary.put("totalSeats", seatAllocationRepository.countByTest_Id(testId));
        summary.put("totalAdmitCards", admitCardRepository.countByTest_Id(testId));
        return summary;
    }

    public Map<String, Object> getEligibilityReport(Long testId) {
        Map<String, Object> report = new HashMap<>();
        report.put("testId", testId);
        report.put("totalRegistered", seatAllocationRepository.countByTest_Id(testId));
        report.put("totalAdmitCards", admitCardRepository.countByTest_Id(testId));
        report.put("totalAttempts", attemptRepository.countByTest_Id(testId));
        return report;
    }

    public Map<String, Object> getAttendanceReport(Long testId) {
        Map<String, Object> report = new HashMap<>();
        report.put("testId", testId);
        report.put("totalAllocated", seatAllocationRepository.countByTest_Id(testId));
        report.put("totalAdmitCards", admitCardRepository.countByTest_Id(testId));
        report.put("totalAttempts", attemptRepository.countByTest_Id(testId));
        return report;
    }
}
