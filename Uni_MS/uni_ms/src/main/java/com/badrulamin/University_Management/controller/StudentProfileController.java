package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.StudentProfile;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.StudentProfileService;
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
@RequestMapping("/api/student-profiles")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<StudentProfile>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StudentProfile> paged = studentProfileService.findAll(pageable);
        PagedResponse<StudentProfile> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<ApiResponse<StudentProfile>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(studentProfileService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<ApiResponse<StudentProfile>> save(@Valid @RequestBody StudentProfile studentProfile) {
        return ResponseEntity.ok(ApiResponse.success(studentProfileService.save(studentProfile)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<ApiResponse<StudentProfile>> update(@PathVariable Long id, @Valid @RequestBody StudentProfile studentProfile) {
        return ResponseEntity.ok(ApiResponse.success(studentProfileService.update(id, studentProfile)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        studentProfileService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
