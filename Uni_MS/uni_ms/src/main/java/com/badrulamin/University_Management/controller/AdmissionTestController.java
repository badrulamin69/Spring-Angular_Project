package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionTest;
import com.badrulamin.University_Management.service.AdmissionTestService;
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
@RequestMapping("/api/admission-tests")
@RequiredArgsConstructor
public class AdmissionTestController {

    private final AdmissionTestService admissionTestService;

    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<AdmissionTest>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionTest> paged = admissionTestService.findAll(pageable);
        PagedResponse<AdmissionTest> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionTest> findById(@PathVariable Long id) {
        return ResponseEntity.ok(admissionTestService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionTest> save(@Valid @RequestBody AdmissionTest admissionTest) {
        return ResponseEntity.ok(admissionTestService.save(admissionTest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionTest> update(@PathVariable Long id, @Valid @RequestBody AdmissionTest admissionTest) {
        return ResponseEntity.ok(admissionTestService.update(id, admissionTest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        admissionTestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
