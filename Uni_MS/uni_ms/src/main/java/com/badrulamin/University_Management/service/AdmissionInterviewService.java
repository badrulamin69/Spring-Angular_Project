package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionInterview;
import com.badrulamin.University_Management.repository.AdmissionInterviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AdmissionInterviewService {

    private final AdmissionInterviewRepository admissionInterviewRepository;

    public AdmissionInterviewService(AdmissionInterviewRepository admissionInterviewRepository) {
        this.admissionInterviewRepository = admissionInterviewRepository;
    }

    public Page<AdmissionInterview> findAll(Pageable pageable) {
        return admissionInterviewRepository.findAll(pageable);
    }

    public AdmissionInterview findById(Long id) {
        return admissionInterviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AdmissionInterview", "id", id));
    }

    public AdmissionInterview create(AdmissionInterview interview) {
        interview.setStatus("SCHEDULED");
        return admissionInterviewRepository.save(interview);
    }

    @Transactional
    public AdmissionInterview update(Long id, AdmissionInterview interview) {
        AdmissionInterview existing = admissionInterviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionInterview", "id", id));
        existing.setApplication(interview.getApplication());
        existing.setInterviewer(interview.getInterviewer());
        existing.setScheduledAt(interview.getScheduledAt());
        existing.setInterviewType(interview.getInterviewType());
        existing.setStatus(interview.getStatus());
        existing.setRemarks(interview.getRemarks());
        existing.setScore(interview.getScore());
        existing.setMaxScore(interview.getMaxScore());
        existing.setStrengths(interview.getStrengths());
        existing.setWeaknesses(interview.getWeaknesses());
        existing.setCompletedAt(interview.getCompletedAt());
        existing.setIsRecommended(interview.getIsRecommended());
        return admissionInterviewRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        admissionInterviewRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return admissionInterviewRepository.countByStatus(status);
    }
}