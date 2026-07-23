package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionTestResult;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.AdmissionTestResultService;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import jakarta.validation.Valid;
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
@RequestMapping("/api/admission-test-results")
@RequiredArgsConstructor
public class AdmissionTestResultController {

    private final AdmissionTestResultService service;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<AdmissionTestResult>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionTestResult> paged = service.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_VIEW')")
    public ResponseEntity<ApiResponse<AdmissionTestResult>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionTestResult>> save(@Valid @RequestBody AdmissionTestResult result) {
        return ResponseEntity.ok(ApiResponse.success(service.save(result)));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveBulk(@Valid @RequestBody List<AdmissionTestResult> results) {
        if (results.size() > 100) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Maximum 100 results per bulk upload"));
        }
        return ResponseEntity.ok(ApiResponse.success(service.saveBulk(results)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionTestResult>> update(@PathVariable Long id, @Valid @RequestBody AdmissionTestResult result) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, result)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
