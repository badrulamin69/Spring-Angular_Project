package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.DepartmentAllocation;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enroll/{allocationId}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> enrollFromAllocation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.enrollFromAllocation(allocationId)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<DepartmentAllocation>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DepartmentAllocation> paged = enrollmentService.findConfirmedNotEnrolled(pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast())));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.getEnrollmentStats()));
    }
}
