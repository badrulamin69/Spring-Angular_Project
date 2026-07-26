package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionApplication;
import com.badrulamin.University_Management.repository.AdmissionApplicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AdmissionApplicationService {

    private final AdmissionApplicationRepository admissionApplicationRepository;

    public AdmissionApplicationService(AdmissionApplicationRepository admissionApplicationRepository) {
        this.admissionApplicationRepository = admissionApplicationRepository;
    }

    public Page<AdmissionApplication> findAll(Pageable pageable) {
        return admissionApplicationRepository.findAll(pageable);
    }

    public AdmissionApplication findById(Long id) {
        return admissionApplicationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AdmissionApplication", "id", id));
    }

    public AdmissionApplication create(AdmissionApplication application) {
        application.setApplicationNumber("APP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        application.setSubmittedAt(LocalDateTime.now());
        application.setIsSubmitted(true);
        application.setIsVerified(false);
        application.setStatus("SUBMITTED");
        return admissionApplicationRepository.save(application);
    }

    @Transactional
    public AdmissionApplication update(Long id, AdmissionApplication application) {
        AdmissionApplication existing = admissionApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionApplication", "id", id));
        existing.setApplicant(application.getApplicant());
        existing.setSession(application.getSession());
        existing.setProgram(application.getProgram());
        existing.setDepartment(application.getDepartment());
        existing.setCampus(application.getCampus());
        existing.setStatus(application.getStatus());
        existing.setRemarks(application.getRemarks());
        existing.setIsVerified(application.getIsVerified());
        existing.setExam(application.getExam());
        existing.setTestScore(application.getTestScore());
        existing.setMeritScore(application.getMeritScore());
        existing.setMeritPosition(application.getMeritPosition());
        return admissionApplicationRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        admissionApplicationRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return admissionApplicationRepository.countByStatus(status);
    }

    public List<AdmissionApplication> findUnverified() {
        return admissionApplicationRepository.findByIsVerifiedFalse();
    }

    public List<Map<String, Object>> getMonthlyTrend() {
        LocalDateTime since = LocalDateTime.now().minusMonths(12);
        List<Object[]> rows = admissionApplicationRepository.countByMonth(since);
        Map<Integer, Long> countMap = rows.stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> ((Number) r[1]).longValue()
                ));
        List<Map<String, Object>> result = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", m);
            item.put("count", countMap.getOrDefault(m, 0L));
            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> getProgramBreakdown() {
        List<Object[]> rows = admissionApplicationRepository.countByProgram();
        long maxCount = rows.stream()
                .mapToLong(r -> ((Number) r[1]).longValue())
                .max()
                .orElse(1);
        String[] colors = {"#6366f1", "#3b82f6", "#22c55e", "#f59e0b", "#ec4899", "#14b8a6", "#8b5cf6", "#ef4444"};
        List<Map<String, Object>> result = new ArrayList<>();
        int i = 0;
        for (Object[] row : rows) {
            String name = (String) row[0];
            long count = ((Number) row[1]).longValue();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", name);
            item.put("count", count);
            item.put("percent", maxCount > 0 ? (count * 100.0 / maxCount) : 0);
            item.put("color", colors[i % colors.length]);
            result.add(item);
            i++;
        }
        return result;
    }

    public Map<String, Long> getStatusCounts() {
        List<Object[]> rows = admissionApplicationRepository.countByStatusGrouped();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], ((Number) row[1]).longValue());
        }
        return result;
    }

    public Page<AdmissionApplication> search(String search, String status, Long programId, Long sessionId, Pageable pageable) {
        return admissionApplicationRepository.search(search, status, programId, sessionId, pageable);
    }
}