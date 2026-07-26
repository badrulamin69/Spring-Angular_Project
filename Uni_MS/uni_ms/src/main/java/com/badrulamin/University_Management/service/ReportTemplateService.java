package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.ReportTemplate;
import com.badrulamin.University_Management.repository.ReportTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportTemplateService {

    private final ReportTemplateRepository reportTemplateRepository;

    public Page<ReportTemplate> findAll(Pageable pageable) {
        return reportTemplateRepository.findAll(pageable);
    }

    public ReportTemplate findById(Long id) {
        return reportTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReportTemplate", "id", id));
    }

    @Transactional
    public ReportTemplate save(ReportTemplate reportTemplate) {
        return reportTemplateRepository.save(reportTemplate);
    }

    @Transactional
    public ReportTemplate update(Long id, ReportTemplate reportTemplate) {
        findById(id);
        reportTemplate.setId(id);
        return reportTemplateRepository.save(reportTemplate);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        reportTemplateRepository.deleteById(id);
    }
}