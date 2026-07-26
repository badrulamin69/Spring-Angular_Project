package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionInterview;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.AdmissionInterviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admission-interviews")
public class AdmissionInterviewController {

    private final AdmissionInterviewService admissionInterviewService;

    public AdmissionInterviewController(AdmissionInterviewService admissionInterviewService) {
        this.admissionInterviewService = admissionInterviewService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<AdmissionInterview> interviews = admissionInterviewService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        return ResponseEntity.ok(new PagedResponse<>(interviews.getContent(), interviews.getNumber(), interviews.getSize(),
                interviews.getTotalElements(), interviews.getTotalPages(), interviews.isFirst(), interviews.isLast()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<ApiResponse<AdmissionInterview>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(admissionInterviewService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_CREATE')")
    public ResponseEntity<ApiResponse<AdmissionInterview>> create(@Valid @RequestBody AdmissionInterview interview) {
        return ResponseEntity.ok(ApiResponse.success(admissionInterviewService.create(interview)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_EDIT')")
    public ResponseEntity<ApiResponse<AdmissionInterview>> update(@PathVariable Long id, @Valid @RequestBody AdmissionInterview interview) {
        return ResponseEntity.ok(ApiResponse.success(admissionInterviewService.update(id, interview)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        admissionInterviewService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of(
                "total", admissionInterviewService.countByStatus("SCHEDULED") + admissionInterviewService.countByStatus("COMPLETED") + admissionInterviewService.countByStatus("CANCELLED"),
                "scheduled", admissionInterviewService.countByStatus("SCHEDULED"),
                "completed", admissionInterviewService.countByStatus("COMPLETED"),
                "cancelled", admissionInterviewService.countByStatus("CANCELLED")
        ));
    }
}
