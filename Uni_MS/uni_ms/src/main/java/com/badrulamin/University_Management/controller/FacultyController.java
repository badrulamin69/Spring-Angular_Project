package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Faculty;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.FacultyService;
import com.badrulamin.University_Management.payload.response.PagedResponse;
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
@RequestMapping("/api/faculties")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Faculty>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean status) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Faculty> paged = facultyService.searchFaculties(search, status, pageable);
        PagedResponse<Faculty> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Faculty>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(facultyService.findById(id)));
    }

    @PreAuthorize("hasAuthority('FACULTY_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<Faculty>> save(@Valid @RequestBody Faculty faculty) {
        return ResponseEntity.ok(ApiResponse.success(facultyService.save(faculty)));
    }

    @PreAuthorize("hasAuthority('FACULTY_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Faculty>> update(@PathVariable Long id, @Valid @RequestBody Faculty faculty) {
        return ResponseEntity.ok(ApiResponse.success(facultyService.update(id, faculty)));
    }

    @PreAuthorize("hasAuthority('FACULTY_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        facultyService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
