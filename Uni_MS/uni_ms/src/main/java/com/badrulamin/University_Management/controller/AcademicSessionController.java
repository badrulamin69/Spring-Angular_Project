package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AcademicSession;
import com.badrulamin.University_Management.service.AcademicSessionService;
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
@RequestMapping("/api/academic-sessions")
@RequiredArgsConstructor
public class AcademicSessionController {

    private final AcademicSessionService academicSessionService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<AcademicSession>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AcademicSession> paged = academicSessionService.findAll(pageable);
        PagedResponse<AcademicSession> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<AcademicSession> findById(@PathVariable Long id) {
        return ResponseEntity.ok(academicSessionService.findById(id));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PostMapping
    public ResponseEntity<AcademicSession> save(@Valid @RequestBody AcademicSession academicSession) {
        return ResponseEntity.ok(academicSessionService.save(academicSession));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<AcademicSession> update(@PathVariable Long id, @Valid @RequestBody AcademicSession academicSession) {
        return ResponseEntity.ok(academicSessionService.update(id, academicSession));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        academicSessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
