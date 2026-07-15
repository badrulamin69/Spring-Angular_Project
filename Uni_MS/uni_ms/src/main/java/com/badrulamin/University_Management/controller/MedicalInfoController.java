package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.MedicalInfo;
import com.badrulamin.University_Management.service.MedicalInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/medical-info")
public class MedicalInfoController {

    private final MedicalInfoService medicalInfoService;

    public MedicalInfoController(MedicalInfoService medicalInfoService) {
        this.medicalInfoService = medicalInfoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<MedicalInfo> items = medicalInfoService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        Map<String, Object> response = new HashMap<>();
        response.put("content", items.getContent());
        response.put("totalElements", items.getTotalElements());
        response.put("totalPages", items.getTotalPages());
        response.put("currentPage", items.getNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<MedicalInfo> findById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalInfoService.findById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<MedicalInfo> findByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(medicalInfoService.findByStudentId(studentId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<MedicalInfo> create(@RequestBody MedicalInfo medicalInfo) {
        return ResponseEntity.ok(medicalInfoService.create(medicalInfo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<MedicalInfo> update(@PathVariable Long id, @RequestBody MedicalInfo medicalInfo) {
        return ResponseEntity.ok(medicalInfoService.update(id, medicalInfo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicalInfoService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        Page<MedicalInfo> all = medicalInfoService.findAll(PageRequest.of(0, 1));
        stats.put("totalRecords", all.getTotalElements());
        return ResponseEntity.ok(stats);
    }
}
