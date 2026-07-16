package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionTestAttempt;
import com.badrulamin.University_Management.service.AdmissionTestAttemptService;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admission-test-attempts")
@RequiredArgsConstructor
public class AdmissionTestAttemptController {

    private final AdmissionTestAttemptService service;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Long testId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<AdmissionTestAttempt> paged = service.findAll(pageable);
        return ResponseEntity.ok(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionTestAttempt> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
