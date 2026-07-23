package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Payroll;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.PayrollService;
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
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Payroll>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payroll> paged = payrollService.findAll(pageable);
        PagedResponse<Payroll> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Payroll>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.findById(id)));
    }

    @PreAuthorize("hasAuthority('PAYROLL_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<Payroll>> save(@Valid @RequestBody Payroll payroll) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.save(payroll)));
    }

    @PreAuthorize("hasAuthority('PAYROLL_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Payroll>> update(@PathVariable Long id, @Valid @RequestBody Payroll payroll) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.update(id, payroll)));
    }

    @PreAuthorize("hasAuthority('PAYROLL_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        payrollService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
