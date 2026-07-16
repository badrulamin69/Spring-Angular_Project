package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AcademicPolicy;
import com.badrulamin.University_Management.repository.AcademicPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class AcademicPolicyService {

    private final AcademicPolicyRepository academicPolicyRepository;

    public Page<AcademicPolicy> findAll(Pageable pageable) {
        return academicPolicyRepository.findAll(pageable);
    }

    public AcademicPolicy findById(Long id) {
        return academicPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPolicy", "id", id));
    }

    public AcademicPolicy save(AcademicPolicy academicPolicy) {
        return academicPolicyRepository.save(academicPolicy);
    }

    public AcademicPolicy update(Long id, AcademicPolicy academicPolicy) {
        findById(id);
        academicPolicy.setId(id);
        return academicPolicyRepository.save(academicPolicy);
    }

    public void delete(Long id) {
        findById(id);
        academicPolicyRepository.deleteById(id);
    }
}
