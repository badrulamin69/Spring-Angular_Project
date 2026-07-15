package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AcademicCalendar;
import com.badrulamin.University_Management.service.AcademicCalendarService;
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
@RequestMapping("/api/academic-calendars")
@RequiredArgsConstructor
public class AcademicCalendarController {

    private final AcademicCalendarService academicCalendarService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<AcademicCalendar>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AcademicCalendar> paged = academicCalendarService.findAll(pageable);
        PagedResponse<AcademicCalendar> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<AcademicCalendar> findById(@PathVariable Long id) {
        return ResponseEntity.ok(academicCalendarService.findById(id));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PostMapping
    public ResponseEntity<AcademicCalendar> save(@Valid @RequestBody AcademicCalendar academicCalendar) {
        return ResponseEntity.ok(academicCalendarService.save(academicCalendar));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<AcademicCalendar> update(@PathVariable Long id, @Valid @RequestBody AcademicCalendar academicCalendar) {
        return ResponseEntity.ok(academicCalendarService.update(id, academicCalendar));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        academicCalendarService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
