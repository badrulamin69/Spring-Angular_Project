package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Prerequisite;
import com.badrulamin.University_Management.service.PrerequisiteService;
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
@RequestMapping("/api/prerequisites")
@RequiredArgsConstructor
public class PrerequisiteController {

    private final PrerequisiteService prerequisiteService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<Prerequisite>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Prerequisite> paged = prerequisiteService.findAll(pageable);
        PagedResponse<Prerequisite> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<Prerequisite> findById(@PathVariable Long id) {
        return ResponseEntity.ok(prerequisiteService.findById(id));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PostMapping
    public ResponseEntity<Prerequisite> save(@Valid @RequestBody Prerequisite prerequisite) {
        return ResponseEntity.ok(prerequisiteService.save(prerequisite));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<Prerequisite> update(@PathVariable Long id, @Valid @RequestBody Prerequisite prerequisite) {
        return ResponseEntity.ok(prerequisiteService.update(id, prerequisite));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prerequisiteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
