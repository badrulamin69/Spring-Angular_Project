package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.GradeRule;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.GradeRuleService;
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
@RequestMapping("/api/grade-rules")
@RequiredArgsConstructor
public class GradeRuleController {

    private final GradeRuleService gradeRuleService;

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<GradeRule>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<GradeRule> paged = gradeRuleService.findAll(pageable);
        PagedResponse<GradeRule> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<GradeRule> findById(@PathVariable Long id) {
        return ResponseEntity.ok(gradeRuleService.findById(id));
    }

    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    @PostMapping
    public ResponseEntity<GradeRule> save(@Valid @RequestBody GradeRule gradeRule) {
        return ResponseEntity.ok(gradeRuleService.save(gradeRule));
    }

    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<GradeRule> update(@PathVariable Long id, @Valid @RequestBody GradeRule gradeRule) {
        return ResponseEntity.ok(gradeRuleService.update(id, gradeRule));
    }

    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gradeRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
