package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.EmployeeAttendance;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.EmployeeAttendanceService;
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
@RequestMapping("/api/employee-attendance")
@RequiredArgsConstructor
public class EmployeeAttendanceController {

    private final EmployeeAttendanceService employeeAttendanceService;

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeAttendance>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EmployeeAttendance> paged = employeeAttendanceService.findAll(pageable);
        PagedResponse<EmployeeAttendance> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeAttendance>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(employeeAttendanceService.findById(id)));
    }

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeAttendance>> save(@Valid @RequestBody EmployeeAttendance employeeAttendance) {
        return ResponseEntity.ok(ApiResponse.success(employeeAttendanceService.save(employeeAttendance)));
    }

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeAttendance>> update(@PathVariable Long id, @Valid @RequestBody EmployeeAttendance employeeAttendance) {
        return ResponseEntity.ok(ApiResponse.success(employeeAttendanceService.update(id, employeeAttendance)));
    }

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        employeeAttendanceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
