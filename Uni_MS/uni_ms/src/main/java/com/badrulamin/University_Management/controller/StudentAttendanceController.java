package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.StudentAttendance;
import com.badrulamin.University_Management.service.StudentAttendanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student-attendance")
public class StudentAttendanceController {

    private final StudentAttendanceService studentAttendanceService;

    public StudentAttendanceController(StudentAttendanceService studentAttendanceService) {
        this.studentAttendanceService = studentAttendanceService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<StudentAttendance> items = studentAttendanceService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sort)));
        Map<String, Object> response = new HashMap<>();
        response.put("content", items.getContent());
        response.put("totalElements", items.getTotalElements());
        response.put("totalPages", items.getTotalPages());
        response.put("currentPage", items.getNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<StudentAttendance> findById(@PathVariable Long id) {
        return ResponseEntity.ok(studentAttendanceService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<StudentAttendance> create(@RequestBody StudentAttendance studentAttendance) {
        return ResponseEntity.ok(studentAttendanceService.create(studentAttendance));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<StudentAttendance> update(@PathVariable Long id, @RequestBody StudentAttendance studentAttendance) {
        return ResponseEntity.ok(studentAttendanceService.update(id, studentAttendance));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentAttendanceService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPresent", studentAttendanceService.countByStatus("PRESENT"));
        stats.put("totalAbsent", studentAttendanceService.countByStatus("ABSENT"));
        stats.put("totalLate", studentAttendanceService.countByStatus("LATE"));
        stats.put("totalExcused", studentAttendanceService.countByStatus("EXCUSED"));
        return ResponseEntity.ok(stats);
    }
}
