package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Alumni;
import com.badrulamin.University_Management.service.AlumniService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/alumni")
public class AlumniController {

    private final AlumniService alumniService;

    public AlumniController(AlumniService alumniService) {
        this.alumniService = alumniService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<Alumni> items = alumniService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        Map<String, Object> response = new HashMap<>();
        response.put("content", items.getContent());
        response.put("totalElements", items.getTotalElements());
        response.put("totalPages", items.getTotalPages());
        response.put("currentPage", items.getNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<Alumni> findById(@PathVariable Long id) {
        return ResponseEntity.ok(alumniService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<Alumni> create(@RequestBody Alumni alumni) {
        return ResponseEntity.ok(alumniService.create(alumni));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<Alumni> update(@PathVariable Long id, @RequestBody Alumni alumni) {
        return ResponseEntity.ok(alumniService.update(id, alumni));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alumniService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        Page<Alumni> all = alumniService.findAll(PageRequest.of(0, 1));
        stats.put("totalAlumni", all.getTotalElements());
        stats.put("availableForMentoring", alumniService.findAll(PageRequest.of(0, Integer.MAX_VALUE)).getContent().stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsAvailableForMentoring())).count());
        stats.put("availableForRecruitment", alumniService.findAll(PageRequest.of(0, Integer.MAX_VALUE)).getContent().stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsAvailableForRecruitment())).count());
        return ResponseEntity.ok(stats);
    }
}
