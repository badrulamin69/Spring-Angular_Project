package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Permission;
import com.badrulamin.University_Management.entity.UserPermission;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.UserPermissionService;
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
@RequestMapping("/api/user-permissions")
@RequiredArgsConstructor
public class UserPermissionController {

    private final UserPermissionService userPermissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<PagedResponse<UserPermission>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserPermission> paged = userPermissionService.findAll(pageable);
        PagedResponse<UserPermission> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<UserPermission> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userPermissionService.findById(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<List<UserPermission>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userPermissionService.findByUserId(userId));
    }

    @GetMapping("/user/{userId}/effective")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<List<Permission>> getEffectivePermissions(@PathVariable Long userId) {
        return ResponseEntity.ok(userPermissionService.getEffectivePermissions(userId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<UserPermission> save(@Valid @RequestBody UserPermission userPermission) {
        return ResponseEntity.ok(userPermissionService.save(userPermission));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<UserPermission> update(@PathVariable Long id, @Valid @RequestBody UserPermission userPermission) {
        return ResponseEntity.ok(userPermissionService.update(id, userPermission));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userPermissionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<Void> bulkSave(@Valid @RequestBody BulkUserPermissionRequest request) {
        userPermissionService.bulkSave(request.getUserId(), request.getPermissionIds(), request.isGranted());
        return ResponseEntity.ok().build();
    }

    public static class BulkUserPermissionRequest {
        private Long userId;
        private List<Long> permissionIds;
        private boolean granted;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public List<Long> getPermissionIds() { return permissionIds; }
        public void setPermissionIds(List<Long> permissionIds) { this.permissionIds = permissionIds; }
        public boolean isGranted() { return granted; }
        public void setGranted(boolean granted) { this.granted = granted; }
    }
}
