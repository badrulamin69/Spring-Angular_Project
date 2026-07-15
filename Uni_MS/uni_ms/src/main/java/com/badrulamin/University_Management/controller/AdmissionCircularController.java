package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionCircular;
import com.badrulamin.University_Management.service.AdmissionCircularService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admission-circulars")
public class AdmissionCircularController {

    private final AdmissionCircularService admissionCircularService;

    public AdmissionCircularController(AdmissionCircularService admissionCircularService) {
        this.admissionCircularService = admissionCircularService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<AdmissionCircular> circulars = admissionCircularService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        return ResponseEntity.ok(Map.of(
                "content", circulars.getContent(),
                "totalElements", circulars.getTotalElements(),
                "totalPages", circulars.getTotalPages(),
                "currentPage", circulars.getNumber()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionCircular> findById(@PathVariable Long id) {
        return ResponseEntity.ok(admissionCircularService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_CREATE')")
    public ResponseEntity<AdmissionCircular> create(@Valid @RequestBody AdmissionCircular circular) {
        return ResponseEntity.ok(admissionCircularService.create(circular));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_EDIT')")
    public ResponseEntity<AdmissionCircular> update(@PathVariable Long id, @Valid @RequestBody AdmissionCircular circular) {
        return ResponseEntity.ok(admissionCircularService.update(id, circular));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        admissionCircularService.delete(id);
        return ResponseEntity.ok().build();
    }
}
