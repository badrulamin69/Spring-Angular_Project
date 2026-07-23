package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Mark;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.MarkService;
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
@RequestMapping("/api/marks")
@RequiredArgsConstructor
public class MarkController {

    private final MarkService markService;

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Mark>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Mark> paged = markService.findAll(pageable);
        PagedResponse<Mark> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Mark>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(markService.findById(id)));
    }

    @PreAuthorize("hasAuthority('MARKS_ENTER')")
    @PostMapping
    public ResponseEntity<ApiResponse<Mark>> save(@Valid @RequestBody Mark mark) {
        return ResponseEntity.ok(ApiResponse.success(markService.save(mark)));
    }

    @PreAuthorize("hasAuthority('MARKS_ENTER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Mark>> update(@PathVariable Long id, @Valid @RequestBody Mark mark) {
        return ResponseEntity.ok(ApiResponse.success(markService.update(id, mark)));
    }

    @PreAuthorize("hasAuthority('MARKS_ENTER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        markService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
