package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Student;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.payload.response.StudentResponse;
import com.badrulamin.University_Management.service.StudentService;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<StudentResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long departmentId) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Student> paged = studentService.searchStudents(search, status, departmentId, pageable);
        Page<StudentResponse> dtoPage = paged.map(studentService::toResponse);
        PagedResponse<StudentResponse> response = new PagedResponse<>(dtoPage.getContent(), dtoPage.getNumber(), dtoPage.getSize(), dtoPage.getTotalElements(), dtoPage.getTotalPages(), dtoPage.isFirst(), dtoPage.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public ResponseEntity<ApiResponse<StudentResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(studentService.toResponse(studentService.findById(id))));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    public ResponseEntity<ApiResponse<StudentResponse>> save(@Valid @RequestBody Student student) {
        return ResponseEntity.ok(ApiResponse.success(studentService.toResponse(studentService.save(student))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_EDIT')")
    public ResponseEntity<ApiResponse<StudentResponse>> update(@PathVariable Long id, @Valid @RequestBody Student student) {
        return ResponseEntity.ok(ApiResponse.success(studentService.toResponse(studentService.update(id, student))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
