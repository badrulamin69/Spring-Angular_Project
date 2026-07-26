package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.entity.AdmissionTestResult;
import com.badrulamin.University_Management.repository.AdmissionTestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdmissionTestResultService {

    private final AdmissionTestResultRepository repository;

    public Page<AdmissionTestResult> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public AdmissionTestResult findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTestResult", "id", id));
    }

    public AdmissionTestResult findByRegistrationId(Long registrationId) {
        return repository.findByRegistration_Id(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTestResult", "registrationId", registrationId));
    }

    @Transactional
    public AdmissionTestResult save(AdmissionTestResult result) {
        if (result.getStatus() == null) {
            result.setStatus("SCORED");
        }
        return repository.save(result);
    }

    @Transactional
    public AdmissionTestResult update(Long id, AdmissionTestResult result) {
        findById(id);
        result.setId(id);
        return repository.save(result);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    public long countByStatus(String status) {
        return repository.countByStatus(status);
    }

    public Map<String, Object> saveBulk(List<AdmissionTestResult> results) {
        int successCount = 0;
        int errorCount = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            try {
                AdmissionTestResult result = results.get(i);
                if (result.getStatus() == null) {
                    result.setStatus("SCORED");
                }
                repository.save(result);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                errors.add("Row " + (i + 1) + ": " + e.getMessage());
            }
        }

        return Map.of(
                "successCount", successCount,
                "errorCount", errorCount,
                "errors", errors,
                "message", successCount + " results saved" + (errorCount > 0 ? ", " + errorCount + " failed" : "")
        );
    }
}