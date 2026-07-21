package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.payload.request.ClassroomRequest;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.ClassroomResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.ClassroomService;
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
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<ClassroomResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ClassroomResponse> paged = classroomService.findAll(pageable);
        PagedResponse<ClassroomResponse> response = new PagedResponse<>(
                paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(),
                paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    public ResponseEntity<ApiResponse<ClassroomResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(classroomService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    public ResponseEntity<ApiResponse<ClassroomResponse>> create(@Valid @RequestBody ClassroomRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Classroom created successfully", classroomService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    public ResponseEntity<ApiResponse<ClassroomResponse>> update(@PathVariable Long id, @Valid @RequestBody ClassroomRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Classroom updated successfully", classroomService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        classroomService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Classroom deleted successfully", null));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    public ResponseEntity<ApiResponse<List<ClassroomResponse>>> findAvailable() {
        return ResponseEntity.ok(ApiResponse.success(classroomService.findAvailable()));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    public ResponseEntity<ApiResponse<List<ClassroomResponse>>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Boolean isLab,
            @RequestParam(required = false) Boolean isSmartClassroom,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(ApiResponse.success(classroomService.search(search, buildingId, roomType, isLab, isSmartClassroom, isActive)));
    }
}
