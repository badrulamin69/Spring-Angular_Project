package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionTest;
import com.badrulamin.University_Management.repository.AdmissionTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdmissionTestService {

    private final AdmissionTestRepository admissionTestRepository;

    public Page<AdmissionTest> findAll(Pageable pageable) {
        return admissionTestRepository.findAll(pageable);
    }

    public AdmissionTest findById(Long id) {
        return admissionTestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AdmissionTest not found with id: " + id));
    }

    public AdmissionTest save(AdmissionTest admissionTest) {
        return admissionTestRepository.save(admissionTest);
    }

    public AdmissionTest update(Long id, AdmissionTest admissionTest) {
        findById(id);
        admissionTest.setId(id);
        return admissionTestRepository.save(admissionTest);
    }

    public void delete(Long id) {
        findById(id);
        admissionTestRepository.deleteById(id);
    }
}
