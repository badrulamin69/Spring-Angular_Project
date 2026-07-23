package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.DocumentVerification;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.DocumentVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/document-verifications")
@RequiredArgsConstructor
public class DocumentVerificationController {

    private final DocumentVerificationService documentVerificationService;

    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<DocumentVerification>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DocumentVerification> paged = documentVerificationService.findAll(pageable);
        PagedResponse<DocumentVerification> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentVerification>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(documentVerificationService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<DocumentVerification>> save(@Valid @RequestBody DocumentVerification documentVerification) {
        return ResponseEntity.ok(ApiResponse.success(documentVerificationService.save(documentVerification)));
    }

    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentVerification>> update(@PathVariable Long id, @Valid @RequestBody DocumentVerification documentVerification) {
        return ResponseEntity.ok(ApiResponse.success(documentVerificationService.update(id, documentVerification)));
    }

    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        documentVerificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
