package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.ReportTemplate;
import com.badrulamin.University_Management.repository.ReportTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportTemplateService {

    private final ReportTemplateRepository reportTemplateRepository;

    public Page<ReportTemplate> findAll(Pageable pageable) {
        return reportTemplateRepository.findAll(pageable);
    }

    public ReportTemplate findById(Long id) {
        return reportTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ReportTemplate not found with id: " + id));
    }

    public ReportTemplate save(ReportTemplate reportTemplate) {
        return reportTemplateRepository.save(reportTemplate);
    }

    public ReportTemplate update(Long id, ReportTemplate reportTemplate) {
        findById(id);
        reportTemplate.setId(id);
        return reportTemplateRepository.save(reportTemplate);
    }

    public void delete(Long id) {
        findById(id);
        reportTemplateRepository.deleteById(id);
    }
}
