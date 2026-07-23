package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.SemesterRoutine;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.SemesterRoutineService;
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
@RequestMapping("/api/semester-routines")
@RequiredArgsConstructor
public class SemesterRoutineController {

    private final SemesterRoutineService semesterRoutineService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<SemesterRoutine>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SemesterRoutine> paged = semesterRoutineService.findAll(pageable);
        PagedResponse<SemesterRoutine> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SemesterRoutine>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(semesterRoutineService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<SemesterRoutine>> save(@Valid @RequestBody SemesterRoutine semesterRoutine) {
        return ResponseEntity.ok(ApiResponse.success(semesterRoutineService.save(semesterRoutine)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SemesterRoutine>> update(@PathVariable Long id, @Valid @RequestBody SemesterRoutine semesterRoutine) {
        return ResponseEntity.ok(ApiResponse.success(semesterRoutineService.update(id, semesterRoutine)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        semesterRoutineService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
