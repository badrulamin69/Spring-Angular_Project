package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.DepartmentAllocation;
import com.badrulamin.University_Management.service.DepartmentAllocationService;
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

@RestController
@RequestMapping("/api/department-allocations")
@RequiredArgsConstructor
public class DepartmentAllocationController {

    private final DepartmentAllocationService service;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_VIEW')")
    public ResponseEntity<PagedResponse<DepartmentAllocation>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DepartmentAllocation> paged = service.findAll(pageable);
        return ResponseEntity.ok(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_VIEW')")
    public ResponseEntity<DepartmentAllocation> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<DepartmentAllocation> save(@Valid @RequestBody DepartmentAllocation allocation) {
        return ResponseEntity.ok(service.save(allocation));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<DepartmentAllocation> update(@PathVariable Long id, @Valid @RequestBody DepartmentAllocation allocation) {
        return ResponseEntity.ok(service.update(id, allocation));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<DepartmentAllocation> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirm(id));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<DepartmentAllocation> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }
}
