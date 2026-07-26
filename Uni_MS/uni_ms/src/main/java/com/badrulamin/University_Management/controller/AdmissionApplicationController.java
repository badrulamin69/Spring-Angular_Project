package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionApplication;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.AdmissionApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(new PagedResponse<>(applications.getContent(), applications.getNumber(), applications.getSize(),
                applications.getTotalElements(), applications.getTotalPages(), applications.isFirst(), applications.isLast()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<ApiResponse<AdmissionApplication>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(admissionApplicationService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_CREATE')")
    public ResponseEntity<ApiResponse<AdmissionApplication>> create(@Valid @RequestBody AdmissionApplication application) {
        return ResponseEntity.ok(ApiResponse.success(admissionApplicationService.create(application)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_EDIT')")
    public ResponseEntity<ApiResponse<AdmissionApplication>> update(@PathVariable Long id, @Valid @RequestBody AdmissionApplication application) {
        return ResponseEntity.ok(ApiResponse.success(admissionApplicationService.update(id, application)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        admissionApplicationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @GetMapping("/unverified")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getUnverified() {
        return ResponseEntity.ok(ApiResponse.success(admissionApplicationService.findUnverified()));
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
        return ResponseEntity.ok(ApiResponse.success(admissionApplicationService.getMonthlyTrend()));
    }

    @GetMapping("/analytics/program-breakdown")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getProgramBreakdown() {
        return ResponseEntity.ok(ApiResponse.success(admissionApplicationService.getProgramBreakdown()));
    }

    @GetMapping("/analytics/status-counts")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getStatusCounts() {
        return ResponseEntity.ok(ApiResponse.success(admissionApplicationService.getStatusCounts()));
    }
}
