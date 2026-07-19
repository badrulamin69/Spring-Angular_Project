package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.SeatAllocation;
import com.badrulamin.University_Management.service.SeatAllocationService;
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

import java.util.List;

@RestController
@RequestMapping("/api/seat-allocations")
@RequiredArgsConstructor
public class SeatAllocationController {

    private final SeatAllocationService seatAllocationService;

    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<SeatAllocation>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SeatAllocation> paged = seatAllocationService.findAll(pageable);
        PagedResponse<SeatAllocation> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<SeatAllocation> findById(@PathVariable Long id) {
        return ResponseEntity.ok(seatAllocationService.findById(id));
    }

    @GetMapping("/test/{testId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<List<SeatAllocation>> findByTestId(@PathVariable Long testId) {
        return ResponseEntity.ok(seatAllocationService.findByTestId(testId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<SeatAllocation> save(@Valid @RequestBody SeatAllocation seatAllocation) {
        return ResponseEntity.ok(seatAllocationService.save(seatAllocation));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<SeatAllocation> update(@PathVariable Long id, @Valid @RequestBody SeatAllocation seatAllocation) {
        return ResponseEntity.ok(seatAllocationService.update(id, seatAllocation));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seatAllocationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/auto-generate/{testId}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<List<SeatAllocation>> autoGenerateSeats(@PathVariable Long testId) {
        return ResponseEntity.ok(seatAllocationService.autoGenerateSeats(testId));
    }
}
