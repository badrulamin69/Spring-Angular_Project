package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.GeneratedReport;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.GeneratedReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/generated-reports")
@RequiredArgsConstructor
public class GeneratedReportController {

    private final GeneratedReportService generatedReportService;

    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<GeneratedReport>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<GeneratedReport> paged = generatedReportService.findAll(pageable);
        PagedResponse<GeneratedReport> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<GeneratedReport> findById(@PathVariable Long id) {
        return ResponseEntity.ok(generatedReportService.findById(id));
    }

    @PreAuthorize("hasAuthority('REPORT_GENERATE')")
    @PostMapping
    public ResponseEntity<GeneratedReport> save(@Valid @RequestBody GeneratedReport generatedReport) {
        return ResponseEntity.ok(generatedReportService.save(generatedReport));
    }

    @PreAuthorize("hasAuthority('REPORT_GENERATE')")
    @PutMapping("/{id}")
    public ResponseEntity<GeneratedReport> update(@PathVariable Long id, @Valid @RequestBody GeneratedReport generatedReport) {
        return ResponseEntity.ok(generatedReportService.update(id, generatedReport));
    }

    @PreAuthorize("hasAuthority('REPORT_GENERATE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        generatedReportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
