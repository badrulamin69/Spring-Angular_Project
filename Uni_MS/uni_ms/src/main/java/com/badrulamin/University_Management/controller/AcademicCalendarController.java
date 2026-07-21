package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AcademicCalendar;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.AcademicCalendarService;
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
@RequestMapping("/api/academic-calendars")
@RequiredArgsConstructor
public class AcademicCalendarController {

    private final AcademicCalendarService academicCalendarService;

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AcademicCalendar>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AcademicCalendar> paged = academicCalendarService.findAll(pageable);
        PagedResponse<AcademicCalendar> response = new PagedResponse<>(
                paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AcademicCalendar>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<AcademicCalendar>> save(@Valid @RequestBody AcademicCalendar academicCalendar) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarService.save(academicCalendar)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AcademicCalendar>> update(@PathVariable Long id, @Valid @RequestBody AcademicCalendar academicCalendar) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarService.update(id, academicCalendar)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        academicCalendarService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<ApiResponse<List<AcademicCalendar>>> findBySemester(@PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarService.findBySemester(semesterId)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/holidays/{semesterId}")
    public ResponseEntity<ApiResponse<List<AcademicCalendar>>> findHolidays(@PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarService.findHolidays(semesterId)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/published/{semesterId}")
    public ResponseEntity<ApiResponse<List<AcademicCalendar>>> findPublished(@PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success(academicCalendarService.findPublished(semesterId)));
    }
}
