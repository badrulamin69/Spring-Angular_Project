package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.entity.AdmissionTestResult;
import com.badrulamin.University_Management.repository.AdmissionTestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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

    public AdmissionTestResult save(AdmissionTestResult result) {
        if (result.getStatus() == null) {
            result.setStatus("SCORED");
        }
        return repository.save(result);
    }

    public AdmissionTestResult update(Long id, AdmissionTestResult result) {
        findById(id);
        result.setId(id);
        return repository.save(result);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    public long countByStatus(String status) {
        return repository.countByStatus(status);
    }
}
