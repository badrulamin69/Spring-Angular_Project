package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.GeneratedReport;
import com.badrulamin.University_Management.repository.GeneratedReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneratedReportService {

    private final GeneratedReportRepository generatedReportRepository;

    public Page<GeneratedReport> findAll(Pageable pageable) {
        return generatedReportRepository.findAll(pageable);
    }

    public GeneratedReport findById(Long id) {
        return generatedReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GeneratedReport not found with id: " + id));
    }

    public GeneratedReport save(GeneratedReport generatedReport) {
        return generatedReportRepository.save(generatedReport);
    }

    public GeneratedReport update(Long id, GeneratedReport generatedReport) {
        findById(id);
        generatedReport.setId(id);
        return generatedReportRepository.save(generatedReport);
    }

    public void delete(Long id) {
        findById(id);
        generatedReportRepository.deleteById(id);
    }
}
