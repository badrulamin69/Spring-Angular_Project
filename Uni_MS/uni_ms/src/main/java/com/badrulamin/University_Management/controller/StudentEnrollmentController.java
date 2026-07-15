package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.StudentEnrollment;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.StudentEnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-enrollments")
@RequiredArgsConstructor
public class StudentEnrollmentController {

    private final StudentEnrollmentService studentEnrollmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<PagedResponse<StudentEnrollment>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StudentEnrollment> paged = studentEnrollmentService.findAll(pageable);
        PagedResponse<StudentEnrollment> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<StudentEnrollment> findById(@PathVariable Long id) {
        return ResponseEntity.ok(studentEnrollmentService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    public ResponseEntity<StudentEnrollment> save(@Valid @RequestBody StudentEnrollment studentEnrollment) {
        return ResponseEntity.ok(studentEnrollmentService.save(studentEnrollment));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    public ResponseEntity<StudentEnrollment> update(@PathVariable Long id, @Valid @RequestBody StudentEnrollment studentEnrollment) {
        return ResponseEntity.ok(studentEnrollmentService.update(id, studentEnrollment));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentEnrollmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
