package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionApplication;
import com.badrulamin.University_Management.service.AdmissionApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admission-applications")
public class AdmissionApplicationController {

    private final AdmissionApplicationService admissionApplicationService;

    public AdmissionApplicationController(AdmissionApplicationService admissionApplicationService) {
        this.admissionApplicationService = admissionApplicationService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) Long sessionId) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<AdmissionApplication> applications = admissionApplicationService.search(search, status, programId, sessionId, PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        return ResponseEntity.ok(Map.of(
                "content", applications.getContent(),
                "totalElements", applications.getTotalElements(),
                "totalPages", applications.getTotalPages(),
                "currentPage", applications.getNumber()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionApplication> findById(@PathVariable Long id) {
        return ResponseEntity.ok(admissionApplicationService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_CREATE')")
    public ResponseEntity<AdmissionApplication> create(@Valid @RequestBody AdmissionApplication application) {
        return ResponseEntity.ok(admissionApplicationService.create(application));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_EDIT')")
    public ResponseEntity<AdmissionApplication> update(@PathVariable Long id, @Valid @RequestBody AdmissionApplication application) {
        return ResponseEntity.ok(admissionApplicationService.update(id, application));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        admissionApplicationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unverified")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getUnverified() {
        return ResponseEntity.ok(admissionApplicationService.findUnverified());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of(
                "total", admissionApplicationService.countByStatus("SUBMITTED") + admissionApplicationService.countByStatus("APPROVED") + admissionApplicationService.countByStatus("REJECTED"),
                "submitted", admissionApplicationService.countByStatus("SUBMITTED"),
                "approved", admissionApplicationService.countByStatus("APPROVED"),
                "rejected", admissionApplicationService.countByStatus("REJECTED")
        ));
    }

    @GetMapping("/analytics/monthly-trend")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getMonthlyTrend() {
        return ResponseEntity.ok(admissionApplicationService.getMonthlyTrend());
    }

    @GetMapping("/analytics/program-breakdown")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getProgramBreakdown() {
        return ResponseEntity.ok(admissionApplicationService.getProgramBreakdown());
    }

    @GetMapping("/analytics/status-counts")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getStatusCounts() {
        return ResponseEntity.ok(admissionApplicationService.getStatusCounts());
    }
}
