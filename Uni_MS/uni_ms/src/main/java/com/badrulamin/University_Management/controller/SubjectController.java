package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Subject;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.SubjectService;
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
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<Subject>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Subject> paged = subjectService.findAll(pageable);
        PagedResponse<Subject> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    public ResponseEntity<ApiResponse<Subject>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(subjectService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SUBJECT_MANAGE')")
    public ResponseEntity<ApiResponse<Subject>> save(@Valid @RequestBody Subject subject) {
        return ResponseEntity.ok(ApiResponse.success(subjectService.save(subject)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUBJECT_MANAGE')")
    public ResponseEntity<ApiResponse<Subject>> update(@PathVariable Long id, @Valid @RequestBody Subject subject) {
        return ResponseEntity.ok(ApiResponse.success(subjectService.update(id, subject)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUBJECT_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
