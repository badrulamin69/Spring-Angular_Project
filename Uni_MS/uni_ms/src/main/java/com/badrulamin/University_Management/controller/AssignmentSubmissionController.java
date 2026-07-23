package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AssignmentSubmission;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.AssignmentSubmissionService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/assignment-submissions")
@RequiredArgsConstructor
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService assignmentSubmissionService;

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AssignmentSubmission>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AssignmentSubmission> paged = assignmentSubmissionService.findAll(pageable);
        PagedResponse<AssignmentSubmission> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LMS_VIEW')")
    public ResponseEntity<ApiResponse<AssignmentSubmission>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(assignmentSubmissionService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LMS_VIEW')")
    public ResponseEntity<ApiResponse<AssignmentSubmission>> save(@Valid @RequestBody AssignmentSubmission assignmentSubmission) {
        return ResponseEntity.ok(ApiResponse.success(assignmentSubmissionService.save(assignmentSubmission)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LMS_VIEW')")
    public ResponseEntity<ApiResponse<AssignmentSubmission>> update(@PathVariable Long id, @Valid @RequestBody AssignmentSubmission assignmentSubmission) {
        return ResponseEntity.ok(ApiResponse.success(assignmentSubmissionService.update(id, assignmentSubmission)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LMS_VIEW')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        assignmentSubmissionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
