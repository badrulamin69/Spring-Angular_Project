package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.ExamSchedule;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.ExamScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/exam-schedules")
@RequiredArgsConstructor
public class ExamScheduleController {

    private final ExamScheduleService examScheduleService;

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<ExamSchedule>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ExamSchedule> paged = examScheduleService.findAll(pageable);
        PagedResponse<ExamSchedule> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ExamSchedule> findById(@PathVariable Long id) {
        return ResponseEntity.ok(examScheduleService.findById(id));
    }

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @PostMapping
    public ResponseEntity<ExamSchedule> save(@Valid @RequestBody ExamSchedule examSchedule) {
        return ResponseEntity.ok(examScheduleService.save(examSchedule));
    }

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @PutMapping("/{id}")
    public ResponseEntity<ExamSchedule> update(@PathVariable Long id, @Valid @RequestBody ExamSchedule examSchedule) {
        return ResponseEntity.ok(examScheduleService.update(id, examSchedule));
    }

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examScheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
