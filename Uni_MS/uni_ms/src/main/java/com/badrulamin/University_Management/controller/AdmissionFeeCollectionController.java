package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionFeeCollection;
import com.badrulamin.University_Management.service.AdmissionFeeCollectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admission-fee-collection")
public class AdmissionFeeCollectionController {

    private final AdmissionFeeCollectionService service;

    public AdmissionFeeCollectionController(AdmissionFeeCollectionService service) {
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
        Page<AdmissionFeeCollection> result = service.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        return ResponseEntity.ok(Map.of(
                "content", result.getContent(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "currentPage", result.getNumber()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<AdmissionFeeCollection> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionFeeCollection> create(@RequestBody AdmissionFeeCollection entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionFeeCollection> update(@PathVariable Long id, @RequestBody AdmissionFeeCollection entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
