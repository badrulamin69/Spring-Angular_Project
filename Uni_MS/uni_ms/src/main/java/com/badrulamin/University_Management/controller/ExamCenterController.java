package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.ExamCenter;
import com.badrulamin.University_Management.service.ExamCenterService;
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
@RequestMapping("/api/exam-centers")
@RequiredArgsConstructor
public class ExamCenterController {

    private final ExamCenterService examCenterService;

    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<ExamCenter>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ExamCenter> paged = examCenterService.findAll(pageable);
        PagedResponse<ExamCenter> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_TEST_VIEW')")
    public ResponseEntity<ExamCenter> findById(@PathVariable Long id) {
        return ResponseEntity.ok(examCenterService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<ExamCenter> save(@Valid @RequestBody ExamCenter examCenter) {
        return ResponseEntity.ok(examCenterService.save(examCenter));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<ExamCenter> update(@PathVariable Long id, @Valid @RequestBody ExamCenter examCenter) {
        return ResponseEntity.ok(examCenterService.update(id, examCenter));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE', 'ADMISSION_TEST_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examCenterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
