package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.University;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.UniversityService;
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
@RequestMapping("/api/universities")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<University>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<University> paged = universityService.findAll(pageable);
        PagedResponse<University> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<University>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(universityService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<University>> save(@Valid @RequestBody University university) {
        return ResponseEntity.ok(ApiResponse.success(universityService.save(university)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<University>> update(@PathVariable Long id, @Valid @RequestBody University university) {
        return ResponseEntity.ok(ApiResponse.success(universityService.update(id, university)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        universityService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
