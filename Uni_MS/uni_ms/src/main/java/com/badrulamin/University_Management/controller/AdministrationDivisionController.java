package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdministrationDivision;
import com.badrulamin.University_Management.service.AdministrationDivisionService;
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
@RequestMapping("/api/administration-divisions")
@RequiredArgsConstructor
public class AdministrationDivisionController {

    private final AdministrationDivisionService AdministrationDivisionService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<AdministrationDivision>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdministrationDivision> paged = AdministrationDivisionService.findAll(pageable);
        PagedResponse<AdministrationDivision> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<AdministrationDivision> findById(@PathVariable Long id) {
        return ResponseEntity.ok(AdministrationDivisionService.findById(id));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PostMapping
    public ResponseEntity<AdministrationDivision> save(@Valid @RequestBody AdministrationDivision AdministrationDivision) {
        return ResponseEntity.ok(AdministrationDivisionService.save(AdministrationDivision));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<AdministrationDivision> update(@PathVariable Long id, @Valid @RequestBody AdministrationDivision AdministrationDivision) {
        return ResponseEntity.ok(AdministrationDivisionService.update(id, AdministrationDivision));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        AdministrationDivisionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
