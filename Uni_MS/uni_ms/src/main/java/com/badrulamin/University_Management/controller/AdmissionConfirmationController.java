package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionConfirmation;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.entity.AdmissionDocument;
import com.badrulamin.University_Management.payload.request.AdmissionDocumentSubmitRequest;
import com.badrulamin.University_Management.payload.request.AdmissionDocumentVerificationRequest;
import com.badrulamin.University_Management.payload.request.AdmissionFeePaymentRequest;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.AdmissionConfirmationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admission-confirmations")
@RequiredArgsConstructor
public class AdmissionConfirmationController {

    private final AdmissionConfirmationService confirmationService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<PagedResponse<AdmissionConfirmation>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean documentsVerified,
            @RequestParam(required = false) Boolean feePaid) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionConfirmation> paged = confirmationService.findByFilters(search, status, documentsVerified, feePaid, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionConfirmation>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(confirmationService.getConfirmationById(id)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<ApiResponse<AdmissionConfirmation>> getMyConfirmation(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(confirmationService.getMyConfirmation(authentication.getName())));
    }

    @PostMapping("/initiate/{allocationId}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionConfirmation>> initiateConfirmation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(ApiResponse.success(confirmationService.initiateConfirmation(allocationId)));
    }

    @PostMapping("/{id}/submit-documents")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<ApiResponse<List<AdmissionDocument>>> submitDocuments(
            @PathVariable Long id,
            @NotEmpty(message = "Documents list must not be empty")
            @Valid @RequestBody List<AdmissionDocumentSubmitRequest> documents) {
        return ResponseEntity.ok(ApiResponse.success(confirmationService.submitDocuments(id, documents)));
    }

    @PostMapping("/{id}/verify-documents")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionConfirmation>> verifyDocuments(
            @PathVariable Long id,
            @Valid @RequestBody AdmissionDocumentVerificationRequest body,
            Authentication authentication) {
        Long verifiedBy = getUser(authentication);
        return ResponseEntity.ok(ApiResponse.success(confirmationService.verifyDocuments(id, body.getVerified(), body.getRemarks(), verifiedBy)));
    }

    @PostMapping("/{id}/pay-fee")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionConfirmation>> payFee(
            @PathVariable Long id,
            @Valid @RequestBody AdmissionFeePaymentRequest body) {
        return ResponseEntity.ok(ApiResponse.success(confirmationService.payFee(id, body.getAmount(), body.getPaymentMethod(), body.getTransactionId())));
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> confirmAdmission(
            @PathVariable Long id,
            Authentication authentication) {
        Long confirmedBy = getUser(authentication);
        return ResponseEntity.ok(ApiResponse.success(confirmationService.confirmAdmission(id, confirmedBy)));
    }

    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<List<AdmissionDocument>>> getDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(confirmationService.getDocumentsByConfirmationId(id)));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(confirmationService.getStats()));
    }

    private Long getUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof com.badrulamin.University_Management.entity.User user) {
            return user.getId();
        }
        return null;
    }
}
