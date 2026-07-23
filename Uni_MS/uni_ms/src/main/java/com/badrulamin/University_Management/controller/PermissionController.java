package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Permission;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.PermissionService;
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
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Permission>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Permission> paged = permissionService.findAll(pageable);
        PagedResponse<Permission> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Permission>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.findById(id)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<Permission>> save(@Valid @RequestBody Permission permission) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.save(permission)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Permission>> update(@PathVariable Long id, @Valid @RequestBody Permission permission) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.update(id, permission)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
