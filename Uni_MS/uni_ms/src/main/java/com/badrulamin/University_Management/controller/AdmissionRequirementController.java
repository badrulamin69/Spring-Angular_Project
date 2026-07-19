package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionRequirement;
import com.badrulamin.University_Management.service.AdmissionRequirementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admission-requirements")
public class AdmissionRequirementController {

    private final AdmissionRequirementService service;

    public AdmissionRequirementController(AdmissionRequirementService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<AdmissionRequirement> result = service.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        return ResponseEntity.ok(Map.of(
                "content", result.getContent(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "currentPage", result.getNumber()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionRequirement> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionRequirement> create(@RequestBody AdmissionRequirement entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionRequirement> update(@PathVariable Long id, @RequestBody AdmissionRequirement entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
