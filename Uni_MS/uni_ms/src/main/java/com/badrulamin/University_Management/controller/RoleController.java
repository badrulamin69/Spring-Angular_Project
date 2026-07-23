package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Role;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.RoleService;
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

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Role>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Role> paged = roleService.findAll(pageable);
        PagedResponse<Role> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<Role>> save(@Valid @RequestBody Role role) {
        return ResponseEntity.ok(ApiResponse.success(roleService.save(role)));
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> update(@PathVariable Long id, @Valid @RequestBody Role role) {
        return ResponseEntity.ok(ApiResponse.success(roleService.update(id, role)));
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    @GetMapping("/hierarchy")
    public ResponseEntity<ApiResponse<List<Role>>> getRoleHierarchy() {
        return ResponseEntity.ok(ApiResponse.success(roleService.getRoleHierarchy()));
    }

    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    @GetMapping("/roots")
    public ResponseEntity<ApiResponse<List<Role>>> getRootRoles() {
        return ResponseEntity.ok(ApiResponse.success(roleService.getRootRoles()));
    }

    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse<List<Role>>> getChildRoles(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getChildRoles(id)));
    }

    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    @GetMapping("/level/{level}")
    public ResponseEntity<ApiResponse<List<Role>>> getRolesByLevel(@PathVariable Integer level) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getRolesByLevel(level)));
    }
}
