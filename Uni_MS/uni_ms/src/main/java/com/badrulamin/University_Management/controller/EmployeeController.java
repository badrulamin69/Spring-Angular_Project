package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Employee;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.EmployeeResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.EmployeeService;
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
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String status) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Employee> paged = employeeService.searchEmployees(search, departmentId, designation, status, pageable);
        Page<EmployeeResponse> dtoPage = paged.map(employeeService::toResponse);
        PagedResponse<EmployeeResponse> response = new PagedResponse<>(dtoPage.getContent(), dtoPage.getNumber(), dtoPage.getSize(), dtoPage.getTotalElements(), dtoPage.getTotalPages(), dtoPage.isFirst(), dtoPage.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.toResponse(employeeService.findById(id))));
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> save(@Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.toResponse(employeeService.save(employee))));
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(@PathVariable Long id, @Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.toResponse(employeeService.update(id, employee))));
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
