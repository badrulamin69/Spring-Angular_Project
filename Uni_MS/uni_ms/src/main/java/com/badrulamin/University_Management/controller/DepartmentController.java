package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Department;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.DepartmentService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Department>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long facultyId) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Department> paged = departmentService.searchDepartments(search, facultyId, pageable);
        PagedResponse<Department> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.findById(id)));
    }

    @PreAuthorize("hasAuthority('DEPARTMENT_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<Department>> save(@Valid @RequestBody Department department) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.save(department)));
    }

    @PreAuthorize("hasAuthority('DEPARTMENT_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> update(@PathVariable Long id, @Valid @RequestBody Department department) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.update(id, department)));
    }

    @PreAuthorize("hasAuthority('DEPARTMENT_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
