package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.LeaveRequest;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.LeaveRequestService;
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
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<LeaveRequest>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LeaveRequest> paged = leaveRequestService.findAll(pageable);
        PagedResponse<LeaveRequest> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveRequest>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(leaveRequestService.findById(id)));
    }

    @PreAuthorize("hasAuthority('LEAVE_APPROVE')")
    @PostMapping
    public ResponseEntity<ApiResponse<LeaveRequest>> save(@Valid @RequestBody LeaveRequest leaveRequest) {
        return ResponseEntity.ok(ApiResponse.success(leaveRequestService.save(leaveRequest)));
    }

    @PreAuthorize("hasAuthority('LEAVE_APPROVE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveRequest>> update(@PathVariable Long id, @Valid @RequestBody LeaveRequest leaveRequest) {
        return ResponseEntity.ok(ApiResponse.success(leaveRequestService.update(id, leaveRequest)));
    }

    @PreAuthorize("hasAuthority('LEAVE_APPROVE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        leaveRequestService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
