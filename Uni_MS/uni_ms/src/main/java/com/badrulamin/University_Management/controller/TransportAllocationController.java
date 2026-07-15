package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.TransportAllocation;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.TransportAllocationService;
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
@RequestMapping("/api/transport-allocations")
@RequiredArgsConstructor
public class TransportAllocationController {

    private final TransportAllocationService transportAllocationService;

    @GetMapping
    @PreAuthorize("hasAuthority('TRANSPORT_VIEW')")
    public ResponseEntity<PagedResponse<TransportAllocation>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TransportAllocation> paged = transportAllocationService.findAll(pageable);
        PagedResponse<TransportAllocation> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSPORT_VIEW')")
    public ResponseEntity<TransportAllocation> findById(@PathVariable Long id) {
        return ResponseEntity.ok(transportAllocationService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TRANSPORT_VIEW')")
    public ResponseEntity<TransportAllocation> save(@Valid @RequestBody TransportAllocation transportAllocation) {
        return ResponseEntity.ok(transportAllocationService.save(transportAllocation));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSPORT_VIEW')")
    public ResponseEntity<TransportAllocation> update(@PathVariable Long id, @Valid @RequestBody TransportAllocation transportAllocation) {
        return ResponseEntity.ok(transportAllocationService.update(id, transportAllocation));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSPORT_VIEW')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transportAllocationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
