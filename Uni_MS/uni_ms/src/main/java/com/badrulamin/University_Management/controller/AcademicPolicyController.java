package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AcademicPolicy;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.AcademicPolicyService;
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
@RequestMapping("/api/academic-policies")
@RequiredArgsConstructor
public class AcademicPolicyController {

    private final AcademicPolicyService academicPolicyService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AcademicPolicy>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AcademicPolicy> paged = academicPolicyService.findAll(pageable);
        PagedResponse<AcademicPolicy> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AcademicPolicy>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(academicPolicyService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<AcademicPolicy>> save(@Valid @RequestBody AcademicPolicy academicPolicy) {
        return ResponseEntity.ok(ApiResponse.success(academicPolicyService.save(academicPolicy)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AcademicPolicy>> update(@PathVariable Long id, @Valid @RequestBody AcademicPolicy academicPolicy) {
        return ResponseEntity.ok(ApiResponse.success(academicPolicyService.update(id, academicPolicy)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        academicPolicyService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
