package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.payload.request.ClassRoutineRequest;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.ClassRoutineResponse;
import com.badrulamin.University_Management.payload.response.ConflictCheckResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.ClassRoutineService;
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
@RequestMapping("/api/class-routines")
@RequiredArgsConstructor
public class ClassRoutineController {

    private final ClassRoutineService classRoutineService;

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ClassRoutineResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ClassRoutineResponse> paged = classRoutineService.findAll(pageable);
        PagedResponse<ClassRoutineResponse> response = new PagedResponse<>(
                paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassRoutineResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(classRoutineService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<ClassRoutineResponse>> create(@Valid @RequestBody ClassRoutineRequest request) {
        return ResponseEntity.ok(ApiResponse.success(classRoutineService.create(request)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassRoutineResponse>> update(@PathVariable Long id, @Valid @RequestBody ClassRoutineRequest request) {
        return ResponseEntity.ok(ApiResponse.success(classRoutineService.update(id, request)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        classRoutineService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/semester/{semesterId}/section/{sectionId}")
    public ResponseEntity<ApiResponse<List<ClassRoutineResponse>>> findBySemesterAndSection(
            @PathVariable Long semesterId, @PathVariable Long sectionId) {
        return ResponseEntity.ok(ApiResponse.success(classRoutineService.findBySemesterAndSection(semesterId, sectionId)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/teacher/{teacherId}/semester/{semesterId}")
    public ResponseEntity<ApiResponse<List<ClassRoutineResponse>>> findByTeacherAndSemester(
            @PathVariable Long teacherId, @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(classRoutineService.findByTeacherAndSemester(teacherId, semesterId)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/day/{dayOfWeek}/semester/{semesterId}")
    public ResponseEntity<ApiResponse<List<ClassRoutineResponse>>> findByDayOfWeek(
            @PathVariable String dayOfWeek, @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(classRoutineService.findByDayOfWeek(semesterId, dayOfWeek)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/conflicts")
    public ResponseEntity<ApiResponse<List<ConflictCheckResponse>>> checkConflicts(
            @RequestParam(required = false) Long classroomId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) String dayOfWeek,
            @RequestParam(required = false) Long timeSlotId,
            @RequestParam(required = false) Long excludeId) {
        return ResponseEntity.ok(ApiResponse.success(
                classRoutineService.checkConflicts(classroomId, teacherId, semesterId, sectionId, dayOfWeek, timeSlotId, excludeId)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @PostMapping("/publish/semester/{semesterId}/section/{sectionId}")
    public ResponseEntity<ApiResponse<Void>> publishRoutine(
            @PathVariable Long semesterId, @PathVariable Long sectionId) {
        classRoutineService.publishRoutine(semesterId, sectionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
