package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionAttendance;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.AdmissionAttendanceService;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admission-attendance")
@RequiredArgsConstructor
public class AdmissionAttendanceController {

    private final AdmissionAttendanceService admissionAttendanceService;

    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AdmissionAttendance>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionAttendance> paged = admissionAttendanceService.findAll(pageable);
        PagedResponse<AdmissionAttendance> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<ApiResponse<AdmissionAttendance>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(admissionAttendanceService.findById(id)));
    }

    @GetMapping("/test/{testId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<ApiResponse<List<AdmissionAttendance>>> findByTestId(@PathVariable Long testId) {
        return ResponseEntity.ok(ApiResponse.success(admissionAttendanceService.findByTestId(testId)));
    }

    @PostMapping("/mark")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionAttendance>> markAttendance(
            @RequestParam Long testId,
            @RequestParam Long registrationId,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(admissionAttendanceService.markAttendance(testId, registrationId, status, null)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionAttendance>> update(@PathVariable Long id, @Valid @RequestBody AdmissionAttendance admissionAttendance) {
        return ResponseEntity.ok(ApiResponse.success(admissionAttendanceService.update(id, admissionAttendance)));
    }

    @GetMapping("/stats/{testId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(@PathVariable Long testId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("testId", testId);
        stats.put("present", admissionAttendanceService.countPresentByTestId(testId));
        stats.put("absent", admissionAttendanceService.countAbsentByTestId(testId));
        stats.put("late", admissionAttendanceService.countLateByTestId(testId));
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
