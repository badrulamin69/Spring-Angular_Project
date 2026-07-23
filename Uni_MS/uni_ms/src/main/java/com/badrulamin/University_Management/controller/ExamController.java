package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Exam;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.ExamResponse;
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
    public ResponseEntity<ApiResponse<PagedResponse<ExamResponse>>> findAll(
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
        Page<ExamResponse> dtoPage = paged.map(examService::toResponse);
        PagedResponse<ExamResponse> response = new PagedResponse<>(dtoPage.getContent(), dtoPage.getNumber(), dtoPage.getSize(), dtoPage.getTotalElements(), dtoPage.getTotalPages(), dtoPage.isFirst(), dtoPage.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(examService.toResponse(examService.findById(id))));
    }

    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<ExamResponse>> save(@Valid @RequestBody Exam exam) {
        return ResponseEntity.ok(ApiResponse.success(examService.toResponse(examService.save(exam))));
    }

    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamResponse>> update(@PathVariable Long id, @Valid @RequestBody Exam exam) {
        return ResponseEntity.ok(ApiResponse.success(examService.toResponse(examService.update(id, exam))));
    }

    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        examService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
