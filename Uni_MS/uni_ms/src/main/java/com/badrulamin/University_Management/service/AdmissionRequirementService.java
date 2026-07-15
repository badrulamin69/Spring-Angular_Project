package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionRequirement;
import com.badrulamin.University_Management.repository.AdmissionRequirementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdmissionRequirementService {

    private final AdmissionRequirementRepository repository;

    public AdmissionRequirementService(AdmissionRequirementRepository repository) {
        this.repository = repository;
    }

    public Page<AdmissionRequirement> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public AdmissionRequirement findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("AdmissionRequirement not found with id: " + id));
    }

    public AdmissionRequirement create(AdmissionRequirement entity) {
        return repository.save(entity);
    }

    public AdmissionRequirement update(Long id, AdmissionRequirement entity) {
        AdmissionRequirement existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AdmissionRequirement not found with id: " + id));
        existing.setName(entity.getName());
        existing.setDescription(entity.getDescription());
        existing.setDocumentType(entity.getDocumentType());
        existing.setIsMandatory(entity.getIsMandatory());
        existing.setAppliesTo(entity.getAppliesTo());
        existing.setValidityPeriod(entity.getValidityPeriod());
        existing.setProgram(entity.getProgram());
        existing.setIsActive(entity.getIsActive());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
