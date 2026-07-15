package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionInterview;
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
        return ResponseEntity.ok(Map.of(
                "content", interviews.getContent(),
                "totalElements", interviews.getTotalElements(),
                "totalPages", interviews.getTotalPages(),
                "currentPage", interviews.getNumber()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionInterview> findById(@PathVariable Long id) {
        return ResponseEntity.ok(admissionInterviewService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_CREATE')")
    public ResponseEntity<AdmissionInterview> create(@Valid @RequestBody AdmissionInterview interview) {
        return ResponseEntity.ok(admissionInterviewService.create(interview));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_EDIT')")
    public ResponseEntity<AdmissionInterview> update(@PathVariable Long id, @Valid @RequestBody AdmissionInterview interview) {
        return ResponseEntity.ok(admissionInterviewService.update(id, interview));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        admissionInterviewService.delete(id);
        return ResponseEntity.ok().build();
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
