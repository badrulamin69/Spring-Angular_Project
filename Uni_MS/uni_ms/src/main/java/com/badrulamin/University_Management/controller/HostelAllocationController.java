package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.HostelAllocation;
import com.badrulamin.University_Management.service.HostelAllocationService;
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
@RequestMapping("/api/hostel-allocations")
@RequiredArgsConstructor
public class HostelAllocationController {

    private final HostelAllocationService hostelAllocationService;

    @PreAuthorize("hasAuthority('HOSTEL_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<HostelAllocation>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HostelAllocation> paged = hostelAllocationService.findAll(pageable);
        PagedResponse<HostelAllocation> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('HOSTEL_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<HostelAllocation> findById(@PathVariable Long id) {
        return ResponseEntity.ok(hostelAllocationService.findById(id));
    }

    @PreAuthorize("hasAuthority('HOSTEL_VIEW')")
    @PostMapping
    public ResponseEntity<HostelAllocation> save(@Valid @RequestBody HostelAllocation hostelAllocation) {
        return ResponseEntity.ok(hostelAllocationService.save(hostelAllocation));
    }

    @PreAuthorize("hasAuthority('HOSTEL_VIEW')")
    @PutMapping("/{id}")
    public ResponseEntity<HostelAllocation> update(@PathVariable Long id, @Valid @RequestBody HostelAllocation hostelAllocation) {
        return ResponseEntity.ok(hostelAllocationService.update(id, hostelAllocation));
    }

    @PreAuthorize("hasAuthority('HOSTEL_VIEW')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hostelAllocationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
