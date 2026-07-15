package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AuditLog;
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
    public ResponseEntity<PagedResponse<AuditLog>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AuditLog> paged = auditLogService.findAll(pageable);
        PagedResponse<AuditLog> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<AuditLog> findById(@PathVariable Long id) {
        return ResponseEntity.ok(auditLogService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<AuditLog> save(@Valid @RequestBody AuditLog auditLog) {
        return ResponseEntity.ok(auditLogService.save(auditLog));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<AuditLog> update(@PathVariable Long id, @Valid @RequestBody AuditLog auditLog) {
        return ResponseEntity.ok(auditLogService.update(id, auditLog));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        auditLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
