package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionTest;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.AdmissionTestService;
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

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admission-tests")
@RequiredArgsConstructor
public class AdmissionTestController {

    private final AdmissionTestService admissionTestService;

    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_TEST_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AdmissionTest>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) LocalDate testDate) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionTest> paged = admissionTestService.findByFilters(search, status, facultyId, departmentId, testDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_TEST_VIEW')")
    public ResponseEntity<ApiResponse<AdmissionTest>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(admissionTestService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_TEST_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionTest>> save(@Valid @RequestBody AdmissionTest admissionTest) {
        return ResponseEntity.ok(ApiResponse.success(admissionTestService.save(admissionTest)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_TEST_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionTest>> update(@PathVariable Long id, @Valid @RequestBody AdmissionTest admissionTest) {
        return ResponseEntity.ok(ApiResponse.success(admissionTestService.update(id, admissionTest)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_TEST_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        admissionTestService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_TEST_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionTest>> publish(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(admissionTestService.publish(id)));
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_TEST_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionTest>> close(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(admissionTestService.close(id)));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_TEST_VIEW')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "total", admissionTestService.countByStatus("DRAFT") + admissionTestService.countByStatus("PUBLISHED") + admissionTestService.countByStatus("CLOSED"),
                "draft", admissionTestService.countByStatus("DRAFT"),
                "published", admissionTestService.countByStatus("PUBLISHED"),
                "closed", admissionTestService.countByStatus("CLOSED")
        )));
    }
}
