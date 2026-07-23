package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Hostel;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.HostelService;
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
@RequestMapping("/api/hostels")
@RequiredArgsConstructor
public class HostelController {

    private final HostelService hostelService;

    @PreAuthorize("hasAuthority('HOSTEL_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Hostel>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Hostel> paged = hostelService.findAll(pageable);
        PagedResponse<Hostel> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('HOSTEL_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Hostel>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(hostelService.findById(id)));
    }

    @PreAuthorize("hasAuthority('HOSTEL_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<Hostel>> save(@Valid @RequestBody Hostel hostel) {
        return ResponseEntity.ok(ApiResponse.success(hostelService.save(hostel)));
    }

    @PreAuthorize("hasAuthority('HOSTEL_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Hostel>> update(@PathVariable Long id, @Valid @RequestBody Hostel hostel) {
        return ResponseEntity.ok(ApiResponse.success(hostelService.update(id, hostel)));
    }

    @PreAuthorize("hasAuthority('HOSTEL_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        hostelService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
