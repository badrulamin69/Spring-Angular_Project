package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionCircular;
import com.badrulamin.University_Management.repository.AdmissionCircularRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AdmissionCircularService {

    private final AdmissionCircularRepository admissionCircularRepository;

    public AdmissionCircularService(AdmissionCircularRepository admissionCircularRepository) {
        this.admissionCircularRepository = admissionCircularRepository;
    }

    public Page<AdmissionCircular> findAll(Pageable pageable) {
        return admissionCircularRepository.findAll(pageable);
    }

    public AdmissionCircular findById(Long id) {
        return admissionCircularRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AdmissionCircular", "id", id));
    }

    public AdmissionCircular create(AdmissionCircular circular) {
        return admissionCircularRepository.save(circular);
    }

    public AdmissionCircular update(Long id, AdmissionCircular circular) {
        AdmissionCircular existing = admissionCircularRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionCircular", "id", id));
        existing.setTitle(circular.getTitle());
        existing.setDescription(circular.getDescription());
        existing.setEligibility(circular.getEligibility());
        existing.setRequiredDocuments(circular.getRequiredDocuments());
        existing.setAdmissionProcess(circular.getAdmissionProcess());
        existing.setPublishDate(circular.getPublishDate());
        existing.setValidUntil(circular.getValidUntil());
        existing.setStatus(circular.getStatus());
        existing.setAttachmentUrl(circular.getAttachmentUrl());
        existing.setIsPublished(circular.getIsPublished());
        existing.setSession(circular.getSession());
        existing.setProgram(circular.getProgram());
        return admissionCircularRepository.save(existing);
    }

    public void delete(Long id) {
        admissionCircularRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return admissionCircularRepository.countByStatus(status);
    }
}