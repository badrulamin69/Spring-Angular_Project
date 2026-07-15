package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Assignment;
import com.badrulamin.University_Management.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.badrulamin.University_Management.payload.response.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<Assignment>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Assignment> paged = assignmentService.findAll(pageable);
        PagedResponse<Assignment> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LMS_VIEW')")
    public ResponseEntity<Assignment> findById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ASSIGNMENT_MANAGE')")
    public ResponseEntity<Assignment> save(@Valid @RequestBody Assignment assignment) {
        return ResponseEntity.ok(assignmentService.save(assignment));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ASSIGNMENT_MANAGE')")
    public ResponseEntity<Assignment> update(@PathVariable Long id, @Valid @RequestBody Assignment assignment) {
        return ResponseEntity.ok(assignmentService.update(id, assignment));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ASSIGNMENT_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
