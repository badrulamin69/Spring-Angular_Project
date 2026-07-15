package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.ReportTemplate;
import com.badrulamin.University_Management.service.ReportTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.badrulamin.University_Management.payload.response.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/report-templates")
@RequiredArgsConstructor
public class ReportTemplateController {

    private final ReportTemplateService reportTemplateService;

    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<ReportTemplate>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReportTemplate> paged = reportTemplateService.findAll(pageable);
        PagedResponse<ReportTemplate> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ReportTemplate> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reportTemplateService.findById(id));
    }

    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @PostMapping
    public ResponseEntity<ReportTemplate> save(@Valid @RequestBody ReportTemplate reportTemplate) {
        return ResponseEntity.ok(reportTemplateService.save(reportTemplate));
    }

    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @PutMapping("/{id}")
    public ResponseEntity<ReportTemplate> update(@PathVariable Long id, @Valid @RequestBody ReportTemplate reportTemplate) {
        return ResponseEntity.ok(reportTemplateService.update(id, reportTemplate));
    }

    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reportTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
