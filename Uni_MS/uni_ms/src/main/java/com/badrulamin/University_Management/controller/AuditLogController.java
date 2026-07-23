package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AuditLog;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.AuditLogService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AuditLog>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AuditLog> paged = auditLogService.findAll(pageable);
        PagedResponse<AuditLog> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<ApiResponse<AuditLog>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(auditLogService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<ApiResponse<AuditLog>> save(@Valid @RequestBody AuditLog auditLog) {
        return ResponseEntity.ok(ApiResponse.success(auditLogService.save(auditLog)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<ApiResponse<AuditLog>> update(@PathVariable Long id, @Valid @RequestBody AuditLog auditLog) {
        return ResponseEntity.ok(ApiResponse.success(auditLogService.update(id, auditLog)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        auditLogService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
