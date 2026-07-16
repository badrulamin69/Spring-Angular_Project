package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.AdmissionTestQuestion;
import com.badrulamin.University_Management.service.AdmissionTestQuestionService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admission-test-questions")
@RequiredArgsConstructor
public class AdmissionTestQuestionController {

    private final AdmissionTestQuestionService service;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Long testId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (testId != null) {
            List<AdmissionTestQuestion> questions = service.findByTestId(testId);
            return ResponseEntity.ok(questions);
        }
        Page<AdmissionTestQuestion> paged = service.findAll(pageable);
        return ResponseEntity.ok(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionTestQuestion> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionTestQuestion> save(@Valid @RequestBody AdmissionTestQuestion question) {
        return ResponseEntity.ok(service.save(question));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<AdmissionTestQuestion> update(@PathVariable Long id, @Valid @RequestBody AdmissionTestQuestion question) {
        return ResponseEntity.ok(service.update(id, question));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @PreAuthorize("hasAuthority('ADMISSION_MANAGE') or hasAuthority('ADMISSION_VIEW') or hasAuthority('PRE_ADMISSION_MANAGE')")
    public ResponseEntity<Map<String, Long>> countByTestId(@RequestParam Long testId) {
        return ResponseEntity.ok(Map.of("count", service.countByTestId(testId)));
    }
}
