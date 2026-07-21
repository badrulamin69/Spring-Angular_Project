package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.payload.request.AcademicCalendarEventRequest;
import com.badrulamin.University_Management.payload.response.AcademicCalendarEventResponse;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.AcademicCalendarEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/academic-calendar-events")
@RequiredArgsConstructor
public class AcademicCalendarEventController {

    private final AcademicCalendarEventService academicCalendarEventService;

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AcademicCalendarEventResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AcademicCalendarEventResponse> paged = academicCalendarEventService.findAll(pageable);
        PagedResponse<AcademicCalendarEventResponse> pagedResponse = new PagedResponse<>(
                paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AcademicCalendarEventResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarEventService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<AcademicCalendarEventResponse>> create(
            @Valid @RequestBody AcademicCalendarEventRequest request) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarEventService.create(request)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AcademicCalendarEventResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AcademicCalendarEventRequest request) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarEventService.update(id, request)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        academicCalendarEventService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<ApiResponse<List<AcademicCalendarEventResponse>>> findBySemester(
            @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarEventService.findBySemester(semesterId)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<AcademicCalendarEventResponse>>> findUpcoming() {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarEventService.findUpcoming()));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/holidays/{semesterId}")
    public ResponseEntity<ApiResponse<List<AcademicCalendarEventResponse>>> findHolidays(
            @PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarEventService.findHolidays(semesterId)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<AcademicCalendarEventResponse>>> findByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarEventService.findByDateRange(start, end)));
    }
}
