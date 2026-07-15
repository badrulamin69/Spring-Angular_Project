package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.CourseMaterial;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.CourseMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/course-materials")
@RequiredArgsConstructor
public class CourseMaterialController {

    private final CourseMaterialService courseMaterialService;

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<CourseMaterial>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CourseMaterial> paged = courseMaterialService.findAll(pageable);
        PagedResponse<CourseMaterial> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseMaterial> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courseMaterialService.findById(id));
    }

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @PostMapping
    public ResponseEntity<CourseMaterial> save(@Valid @RequestBody CourseMaterial courseMaterial) {
        return ResponseEntity.ok(courseMaterialService.save(courseMaterial));
    }

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseMaterial> update(@PathVariable Long id, @Valid @RequestBody CourseMaterial courseMaterial) {
        return ResponseEntity.ok(courseMaterialService.update(id, courseMaterial));
    }

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseMaterialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
