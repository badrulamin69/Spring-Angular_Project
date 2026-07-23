package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Workflow;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.entity.WorkflowApproval;
import com.badrulamin.University_Management.entity.WorkflowStep;
import com.badrulamin.University_Management.service.WorkflowService;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Workflow>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Workflow> paged = workflowService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast())));
    }

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Workflow>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.findById(id)));
    }

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<Workflow>> save(@Valid @RequestBody Workflow workflow) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.save(workflow)));
    }

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Workflow>> update(@PathVariable Long id, @Valid @RequestBody Workflow workflow) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.update(id, workflow)));
    }

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        workflowService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @GetMapping("/{id}/steps")
    public ResponseEntity<ApiResponse<List<WorkflowStep>>> getSteps(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.getSteps(id)));
    }

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @PostMapping("/{id}/steps")
    public ResponseEntity<ApiResponse<WorkflowStep>> addStep(@PathVariable Long id, @Valid @RequestBody WorkflowStep step) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.addStep(id, step)));
    }

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @PutMapping("/steps/{stepId}")
    public ResponseEntity<ApiResponse<WorkflowStep>> updateStep(@PathVariable Long stepId, @Valid @RequestBody WorkflowStep step) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.updateStep(stepId, step)));
    }

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @DeleteMapping("/steps/{stepId}")
    public ResponseEntity<ApiResponse<Void>> deleteStep(@PathVariable Long stepId) {
        workflowService.deleteStep(stepId);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @PreAuthorize("hasAuthority('HRM_VIEW')")
    @GetMapping("/approvals")
    public ResponseEntity<ApiResponse<List<WorkflowApproval>>> getApprovals(
            @RequestParam String entityType, @RequestParam Long entityId) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.getApprovals(entityType, entityId)));
    }

    @PreAuthorize("hasAuthority('LEAVE_APPROVE')")
    @PostMapping("/leave-requests/{id}/approve")
    public ResponseEntity<ApiResponse<Map<String, String>>> approveLeaveRequest(
            @PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long approverId = ((com.badrulamin.University_Management.security.services.UserDetailsImpl) auth.getPrincipal()).getId();
        String comments = body != null ? body.get("comments") : null;
        workflowService.approveLeaveRequest(id, approverId, comments);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "Leave request approved successfully")));
    }

    @PreAuthorize("hasAuthority('LEAVE_APPROVE')")
    @PostMapping("/leave-requests/{id}/reject")
    public ResponseEntity<ApiResponse<Map<String, String>>> rejectLeaveRequest(
            @PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long approverId = ((com.badrulamin.University_Management.security.services.UserDetailsImpl) auth.getPrincipal()).getId();
        String reason = body != null ? body.get("reason") : null;
        workflowService.rejectLeaveRequest(id, approverId, reason);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "Leave request rejected")));
    }
}
