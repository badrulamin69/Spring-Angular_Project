package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionConfirmation;
import com.badrulamin.University_Management.entity.AdmissionDocument;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.AdmissionConfirmationService;
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
    public ResponseEntity<PagedResponse<AdmissionConfirmation>> findAll(
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
        return ResponseEntity.ok(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionConfirmation> getById(@PathVariable Long id) {
        return ResponseEntity.ok(confirmationService.getConfirmationById(id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionConfirmation> getMyConfirmation(Authentication authentication) {
        return ResponseEntity.ok(confirmationService.getMyConfirmation(authentication.getName()));
    }

    @PostMapping("/initiate/{allocationId}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionConfirmation> initiateConfirmation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(confirmationService.initiateConfirmation(allocationId));
    }

    @PostMapping("/{id}/submit-documents")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<List<AdmissionDocument>> submitDocuments(
            @PathVariable Long id,
            @RequestBody List<Map<String, String>> documents) {
        return ResponseEntity.ok(confirmationService.submitDocuments(id, documents));
    }

    @PostMapping("/{id}/verify-documents")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionConfirmation> verifyDocuments(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        boolean verified = (boolean) body.get("verified");
        String remarks = (String) body.getOrDefault("remarks", "");
        Long verifiedBy = getUser(authentication);
        return ResponseEntity.ok(confirmationService.verifyDocuments(id, verified, remarks, verifiedBy));
    }

    @PostMapping("/{id}/pay-fee")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionConfirmation> payFee(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Double amount = ((Number) body.get("amount")).doubleValue();
        String paymentMethod = (String) body.get("paymentMethod");
        String transactionId = (String) body.get("transactionId");
        return ResponseEntity.ok(confirmationService.payFee(id, amount, paymentMethod, transactionId));
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<Map<String, Object>> confirmAdmission(
            @PathVariable Long id,
            Authentication authentication) {
        Long confirmedBy = getUser(authentication);
        return ResponseEntity.ok(confirmationService.confirmAdmission(id, confirmedBy));
    }

    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<List<AdmissionDocument>> getDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(confirmationService.getDocumentsByConfirmationId(id));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(confirmationService.getStats());
    }

    private Long getUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof com.badrulamin.University_Management.entity.User user) {
            return user.getId();
        }
        return null;
    }
}
