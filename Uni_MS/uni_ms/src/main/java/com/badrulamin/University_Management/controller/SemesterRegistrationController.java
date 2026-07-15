package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.SemesterRegistration;
import com.badrulamin.University_Management.service.SemesterRegistrationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/semester-registrations")
public class SemesterRegistrationController {

    private final SemesterRegistrationService semesterRegistrationService;

    public SemesterRegistrationController(SemesterRegistrationService semesterRegistrationService) {
        this.semesterRegistrationService = semesterRegistrationService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<SemesterRegistration> items = semesterRegistrationService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        Map<String, Object> response = new HashMap<>();
        response.put("content", items.getContent());
        response.put("totalElements", items.getTotalElements());
        response.put("totalPages", items.getTotalPages());
        response.put("currentPage", items.getNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<SemesterRegistration> findById(@PathVariable Long id) {
        return ResponseEntity.ok(semesterRegistrationService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<SemesterRegistration> create(@RequestBody SemesterRegistration semesterRegistration) {
        return ResponseEntity.ok(semesterRegistrationService.create(semesterRegistration));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<SemesterRegistration> update(@PathVariable Long id, @RequestBody SemesterRegistration semesterRegistration) {
        return ResponseEntity.ok(semesterRegistrationService.update(id, semesterRegistration));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        semesterRegistrationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRegistered", semesterRegistrationService.countByStatus("REGISTERED"));
        stats.put("totalPending", semesterRegistrationService.countByStatus("PENDING"));
        stats.put("totalApproved", semesterRegistrationService.countByStatus("APPROVED"));
        stats.put("totalRejected", semesterRegistrationService.countByStatus("REJECTED"));
        return ResponseEntity.ok(stats);
    }
}
