package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.EligibilityVerification;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.EligibilityVerificationService;
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
@RequestMapping("/api/eligibility-verifications")
@RequiredArgsConstructor
public class EligibilityVerificationController {

    private final EligibilityVerificationService eligibilityVerificationService;

    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<EligibilityVerification>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EligibilityVerification> paged = eligibilityVerificationService.findAll(pageable);
        PagedResponse<EligibilityVerification> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<EligibilityVerification>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(eligibilityVerificationService.findById(id)));
    }

    @GetMapping("/test/{testId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<List<EligibilityVerification>>> findByTestId(@PathVariable Long testId) {
        return ResponseEntity.ok(ApiResponse.success(eligibilityVerificationService.findByTestId(testId)));
    }

    @GetMapping("/test/{testId}/status/{status}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<List<EligibilityVerification>>> findByTestIdAndStatus(@PathVariable Long testId, @PathVariable String status) {
        return ResponseEntity.ok(ApiResponse.success(eligibilityVerificationService.findByTestIdAndStatus(testId, status)));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<EligibilityVerification>> save(@Valid @RequestBody EligibilityVerification eligibilityVerification) {
        return ResponseEntity.ok(ApiResponse.success(eligibilityVerificationService.save(eligibilityVerification)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<EligibilityVerification>> update(@PathVariable Long id, @Valid @RequestBody EligibilityVerification eligibilityVerification) {
        return ResponseEntity.ok(ApiResponse.success(eligibilityVerificationService.update(id, eligibilityVerification)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        eligibilityVerificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<EligibilityVerification>> verifyEligibility(@RequestBody Map<String, Object> request) {
        Long testId = Long.valueOf(request.get("testId").toString());
        Long registrationId = Long.valueOf(request.get("registrationId").toString());
        boolean eligible = Boolean.parseBoolean(request.get("eligible").toString());
        String verifiedBy = request.get("verifiedBy") != null ? request.get("verifiedBy").toString() : null;
        String remarks = request.get("remarks") != null ? request.get("remarks").toString() : null;
        return ResponseEntity.ok(ApiResponse.success(eligibilityVerificationService.verifyEligibility(testId, registrationId, eligible, verifiedBy, remarks)));
    }

    @PostMapping("/auto-verify/{testId}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<List<EligibilityVerification>>> autoVerifyAll(@PathVariable Long testId) {
        return ResponseEntity.ok(ApiResponse.success(eligibilityVerificationService.autoVerifyAll(testId)));
    }

    @GetMapping("/stats/{testId}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(@PathVariable Long testId) {
        return ResponseEntity.ok(ApiResponse.success(eligibilityVerificationService.getStats(testId)));
    }
}
