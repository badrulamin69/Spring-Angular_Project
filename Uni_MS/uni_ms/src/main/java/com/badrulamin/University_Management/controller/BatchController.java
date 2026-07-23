package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Batch;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.BatchService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Batch>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Batch> paged = batchService.findAll(pageable);
        PagedResponse<Batch> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    public ResponseEntity<ApiResponse<Batch>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(batchService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BATCH_MANAGE')")
    public ResponseEntity<ApiResponse<Batch>> save(@Valid @RequestBody Batch batch) {
        return ResponseEntity.ok(ApiResponse.success(batchService.save(batch)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BATCH_MANAGE')")
    public ResponseEntity<ApiResponse<Batch>> update(@PathVariable Long id, @Valid @RequestBody Batch batch) {
        return ResponseEntity.ok(ApiResponse.success(batchService.update(id, batch)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BATCH_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        batchService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
