package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Course;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.CourseResponse;
import com.badrulamin.University_Management.service.CourseService;
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

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CourseResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long programId) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Course> paged = courseService.searchCourses(search, departmentId, programId, pageable);
        Page<CourseResponse> dtoPage = paged.map(courseService::toResponse);
        PagedResponse<CourseResponse> response = new PagedResponse<>(dtoPage.getContent(), dtoPage.getNumber(), dtoPage.getSize(), dtoPage.getTotalElements(), dtoPage.getTotalPages(), dtoPage.isFirst(), dtoPage.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(courseService.toResponse(courseService.findById(id))));
    }

    @PreAuthorize("hasAuthority('COURSE_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> save(@Valid @RequestBody Course course) {
        return ResponseEntity.ok(ApiResponse.success(courseService.toResponse(courseService.save(course))));
    }

    @PreAuthorize("hasAuthority('COURSE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> update(@PathVariable Long id, @Valid @RequestBody Course course) {
        return ResponseEntity.ok(ApiResponse.success(courseService.toResponse(courseService.update(id, course))));
    }

    @PreAuthorize("hasAuthority('COURSE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
