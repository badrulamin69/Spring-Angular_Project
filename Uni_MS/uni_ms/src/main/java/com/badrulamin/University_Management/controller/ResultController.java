package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Result;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.ResultService;
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
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Result>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Result> paged = resultService.findAll(pageable);
        PagedResponse<Result> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Result>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(resultService.findById(id)));
    }

    @PreAuthorize("hasAuthority('RESULT_PUBLISH')")
    @PostMapping
    public ResponseEntity<ApiResponse<Result>> save(@Valid @RequestBody Result result) {
        return ResponseEntity.ok(ApiResponse.success(resultService.save(result)));
    }

    @PreAuthorize("hasAuthority('RESULT_PUBLISH')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Result>> update(@PathVariable Long id, @Valid @RequestBody Result result) {
        return ResponseEntity.ok(ApiResponse.success(resultService.update(id, result)));
    }

    @PreAuthorize("hasAuthority('RESULT_PUBLISH')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        resultService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
