package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionEnrollment;
import com.badrulamin.University_Management.service.AdmissionEnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admission-enrollments")
public class AdmissionEnrollmentController {

    private final AdmissionEnrollmentService admissionEnrollmentService;

    public AdmissionEnrollmentController(AdmissionEnrollmentService admissionEnrollmentService) {
        this.admissionEnrollmentService = admissionEnrollmentService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<AdmissionEnrollment> enrollments = admissionEnrollmentService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        return ResponseEntity.ok(Map.of(
                "content", enrollments.getContent(),
                "totalElements", enrollments.getTotalElements(),
                "totalPages", enrollments.getTotalPages(),
                "currentPage", enrollments.getNumber()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionEnrollment> findById(@PathVariable Long id) {
        return ResponseEntity.ok(admissionEnrollmentService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_CREATE')")
    public ResponseEntity<AdmissionEnrollment> create(@Valid @RequestBody AdmissionEnrollment enrollment) {
        return ResponseEntity.ok(admissionEnrollmentService.create(enrollment));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_EDIT')")
    public ResponseEntity<AdmissionEnrollment> update(@PathVariable Long id, @Valid @RequestBody AdmissionEnrollment enrollment) {
        return ResponseEntity.ok(admissionEnrollmentService.update(id, enrollment));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        admissionEnrollmentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of(
                "total", admissionEnrollmentService.countByStatus("ENROLLED") + admissionEnrollmentService.countByStatus("PENDING") + admissionEnrollmentService.countByStatus("CANCELLED"),
                "enrolled", admissionEnrollmentService.countByStatus("ENROLLED"),
                "pending", admissionEnrollmentService.countByStatus("PENDING"),
                "cancelled", admissionEnrollmentService.countByStatus("CANCELLED")
        ));
    }
}
