package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Exam;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.ExamService;
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
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<Exam>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String examType) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Exam> paged = examService.searchExams(search, courseId, examType, pageable);
        PagedResponse<Exam> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<Exam> findById(@PathVariable Long id) {
        return ResponseEntity.ok(examService.findById(id));
    }

    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    @PostMapping
    public ResponseEntity<Exam> save(@Valid @RequestBody Exam exam) {
        return ResponseEntity.ok(examService.save(exam));
    }

    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<Exam> update(@PathVariable Long id, @Valid @RequestBody Exam exam) {
        return ResponseEntity.ok(examService.update(id, exam));
    }

    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
