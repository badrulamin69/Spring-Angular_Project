package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Teacher;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    @PreAuthorize("hasAuthority('TEACHER_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<Teacher>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String status) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Teacher> paged = teacherService.searchTeachers(search, departmentId, facultyId, designation, status, pageable);
        PagedResponse<Teacher> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TEACHER_VIEW')")
    public ResponseEntity<ApiResponse<Teacher>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(teacherService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TEACHER_MANAGE')")
    public ResponseEntity<ApiResponse<Teacher>> save(@Valid @RequestBody Teacher teacher) {
        return ResponseEntity.ok(ApiResponse.success(teacherService.save(teacher)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TEACHER_MANAGE')")
    public ResponseEntity<ApiResponse<Teacher>> update(@PathVariable Long id, @Valid @RequestBody Teacher teacher) {
        return ResponseEntity.ok(ApiResponse.success(teacherService.update(id, teacher)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TEACHER_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
