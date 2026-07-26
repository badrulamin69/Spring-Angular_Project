package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionCandidate;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.AdmissionCandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admission-candidates")
@RequiredArgsConstructor
public class AdmissionCandidateController {

    private final AdmissionCandidateService admissionCandidateService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<PagedResponse<AdmissionCandidate>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdmissionCandidate> paged = admissionCandidateService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW') or hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionCandidate>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(admissionCandidateService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionCandidate>> save(@Valid @RequestBody AdmissionCandidate admissionCandidate) {
        return ResponseEntity.ok(ApiResponse.success(admissionCandidateService.save(admissionCandidate)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<AdmissionCandidate>> update(@PathVariable Long id, @Valid @RequestBody AdmissionCandidate admissionCandidate) {
        return ResponseEntity.ok(ApiResponse.success(admissionCandidateService.update(id, admissionCandidate)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        admissionCandidateService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
