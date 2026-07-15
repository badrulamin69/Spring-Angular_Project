package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.CourseRegistration;
import com.badrulamin.University_Management.service.CourseRegistrationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/course-registrations")
public class CourseRegistrationController {

    private final CourseRegistrationService courseRegistrationService;

    public CourseRegistrationController(CourseRegistrationService courseRegistrationService) {
        this.courseRegistrationService = courseRegistrationService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<CourseRegistration> items = courseRegistrationService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        Map<String, Object> response = new HashMap<>();
        response.put("content", items.getContent());
        response.put("totalElements", items.getTotalElements());
        response.put("totalPages", items.getTotalPages());
        response.put("currentPage", items.getNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<CourseRegistration> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courseRegistrationService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<CourseRegistration> create(@RequestBody CourseRegistration courseRegistration) {
        return ResponseEntity.ok(courseRegistrationService.create(courseRegistration));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<CourseRegistration> update(@PathVariable Long id, @RequestBody CourseRegistration courseRegistration) {
        return ResponseEntity.ok(courseRegistrationService.update(id, courseRegistration));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseRegistrationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRegistered", courseRegistrationService.countByStatus("REGISTERED"));
        stats.put("totalPending", courseRegistrationService.countByStatus("PENDING"));
        stats.put("totalDropped", courseRegistrationService.countByStatus("DROPPED"));
        stats.put("totalCompleted", courseRegistrationService.countByStatus("COMPLETED"));
        return ResponseEntity.ok(stats);
    }
}
