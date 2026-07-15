package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.CourseAssignment;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.CourseAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/course-assignments")
@RequiredArgsConstructor
public class CourseAssignmentController {

    private final CourseAssignmentService courseAssignmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    public ResponseEntity<PagedResponse<CourseAssignment>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CourseAssignment> paged = courseAssignmentService.findAll(pageable);
        PagedResponse<CourseAssignment> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    public ResponseEntity<CourseAssignment> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courseAssignmentService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COURSE_MANAGE')")
    public ResponseEntity<CourseAssignment> save(@Valid @RequestBody CourseAssignment courseAssignment) {
        return ResponseEntity.ok(courseAssignmentService.save(courseAssignment));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_MANAGE')")
    public ResponseEntity<CourseAssignment> update(@PathVariable Long id, @Valid @RequestBody CourseAssignment courseAssignment) {
        return ResponseEntity.ok(courseAssignmentService.update(id, courseAssignment));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseAssignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
