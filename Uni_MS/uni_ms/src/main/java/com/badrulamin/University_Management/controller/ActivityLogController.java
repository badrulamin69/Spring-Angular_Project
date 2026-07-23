package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.ActivityLog;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.ActivityLogService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<ApiResponse<Page<ActivityLog>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(activityLogService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<ApiResponse<ActivityLog>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(activityLogService.findById(id)));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<ApiResponse<Page<ActivityLog>>> findByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(activityLogService.findByUserId(userId, pageable)));
    }

    @GetMapping("/module/{module}")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<ApiResponse<Page<ActivityLog>>> findByModule(
            @PathVariable String module,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(activityLogService.findByModule(module, pageable)));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<ApiResponse<List<ActivityLog>>> findRecent(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(ApiResponse.success(activityLogService.findRecent(limit)));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(activityLogService.getStats()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('AUDIT_MANAGE')")
    public ResponseEntity<ApiResponse<ActivityLog>> save(@Valid @RequestBody ActivityLog activityLog) {
        return ResponseEntity.ok(ApiResponse.success(activityLogService.save(activityLog)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('AUDIT_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        activityLogService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
