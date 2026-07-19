package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.StudentIdGeneration;
import com.badrulamin.University_Management.service.StudentIdGenerationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student-id-generation")
public class StudentIdGenerationController {

    private final StudentIdGenerationService service;

    public StudentIdGenerationController(StudentIdGenerationService service) {
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
        Page<StudentIdGeneration> result = service.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        return ResponseEntity.ok(Map.of(
                "content", result.getContent(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "currentPage", result.getNumber()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_VIEW')")
    public ResponseEntity<StudentIdGeneration> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<StudentIdGeneration> create(@RequestBody StudentIdGeneration entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<StudentIdGeneration> update(@PathVariable Long id, @RequestBody StudentIdGeneration entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
