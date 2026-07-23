package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Guardian;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.GuardianService;
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
@RequestMapping("/api/guardians")
@RequiredArgsConstructor
public class GuardianController {

    private final GuardianService guardianService;

    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Guardian>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Guardian> paged = guardianService.findAll(pageable);
        PagedResponse<Guardian> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Guardian>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(guardianService.findById(id)));
    }

    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @PostMapping
    public ResponseEntity<ApiResponse<Guardian>> save(@Valid @RequestBody Guardian guardian) {
        return ResponseEntity.ok(ApiResponse.success(guardianService.save(guardian)));
    }

    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Guardian>> update(@PathVariable Long id, @Valid @RequestBody Guardian guardian) {
        return ResponseEntity.ok(ApiResponse.success(guardianService.update(id, guardian)));
    }

    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        guardianService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
