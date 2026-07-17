package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Fine;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.FineService;
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

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<Fine>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Fine> paged = fineService.findAll(pageable);
        PagedResponse<Fine> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<Fine> findById(@PathVariable Long id) {
        return ResponseEntity.ok(fineService.findById(id));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Fine>> findByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(fineService.findByStudentId(studentId));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping
    public ResponseEntity<Fine> save(@Valid @RequestBody Fine fine) {
        return ResponseEntity.ok(fineService.save(fine));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<Fine> update(@PathVariable Long id, @Valid @RequestBody Fine fine) {
        return ResponseEntity.ok(fineService.update(id, fine));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/waive")
    public ResponseEntity<Fine> waiveFine(
            @PathVariable Long id,
            @RequestParam(required = false) String waivedBy) {
        return ResponseEntity.ok(fineService.waiveFine(id, waivedBy));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
