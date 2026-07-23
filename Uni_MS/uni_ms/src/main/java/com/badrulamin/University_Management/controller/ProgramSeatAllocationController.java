package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.DepartmentAllocation;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.entity.PreAdmissionRegistration;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.repository.PreAdmissionRegistrationRepository;
import com.badrulamin.University_Management.repository.UserRepository;
import com.badrulamin.University_Management.service.ProgramSeatAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/program-seat-allocations")
@RequiredArgsConstructor
public class ProgramSeatAllocationController {

    private final ProgramSeatAllocationService allocationService;
    private final UserRepository userRepository;
    private final PreAdmissionRegistrationRepository registrationRepository;

    @GetMapping("/admin/allocations")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<PagedResponse<DepartmentAllocation>>> getAllAllocations(
            @RequestParam Long configId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) Boolean isWaiting) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DepartmentAllocation> paged = allocationService.findByFilters(
                configId, search, status, programId, facultyId, isWaiting, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast())));
    }

    @GetMapping("/admin/allocations/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<DepartmentAllocation>> getAllocationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.findById(id)));
    }

    @GetMapping("/admin/stats/{configId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(@PathVariable Long configId) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.getStats(configId)));
    }

    @PostMapping("/admin/auto-allocate/{configId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> runAutoAllocation(@PathVariable Long configId) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.runAutoAllocation(configId)));
    }

    @PostMapping("/admin/manual-allocate")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<DepartmentAllocation>> manualAllocate(
            @RequestParam Long registrationId,
            @RequestParam Long programId,
            @RequestParam Long configId,
            @RequestParam(defaultValue = "DAY") String shift,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.manualAllocate(registrationId, programId, configId, shift, remarks)));
    }

    @PutMapping("/admin/change-allocation/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<DepartmentAllocation>> changeAllocation(
            @PathVariable Long id,
            @RequestParam Long newProgramId,
            @RequestParam(defaultValue = "DAY") String shift,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.changeAllocation(id, newProgramId, shift, remarks)));
    }

    @PutMapping("/admin/cancel/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<DepartmentAllocation>> cancelAllocation(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.cancelAllocation(id, remarks)));
    }

    @PostMapping("/admin/reallocate/{configId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> runReallocation(@PathVariable Long configId) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.runReallocation(configId)));
    }

    @PostMapping("/admin/expire-overdue/{configId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> expireOverdue(@PathVariable Long configId) {
        allocationService.expireOverdueAllocations(configId);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @GetMapping("/admin/demand-report/{configId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDemandReport(@PathVariable Long configId) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.getDepartmentDemandReport(configId)));
    }

    @GetMapping("/my-allocation")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<DepartmentAllocation>> getMyAllocation(
            @RequestParam Long configId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long registrationId = getUserIdFromUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(allocationService.findByRegistrationAndConfig(registrationId, configId)));
    }

    @PostMapping("/accept/{allocationId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<DepartmentAllocation>> acceptAllocation(
            @PathVariable Long allocationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.acceptAllocation(allocationId)));
    }

    @PostMapping("/decline/{allocationId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<DepartmentAllocation>> declineAllocation(
            @PathVariable Long allocationId,
            @RequestParam(required = false) String remarks,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.declineAllocation(allocationId, remarks)));
    }

    private Long getUserIdFromUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        PreAdmissionRegistration registration = registrationRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "email", user.getEmail()));
        return registration.getId();
    }
}
