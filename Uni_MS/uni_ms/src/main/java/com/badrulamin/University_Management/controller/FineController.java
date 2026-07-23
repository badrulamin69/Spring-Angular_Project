package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Fine;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.FineService;
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

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Fine>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Fine> paged = fineService.findAll(pageable);
        PagedResponse<Fine> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Fine>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(fineService.findById(id)));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Fine>>> findByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(fineService.findByStudentId(studentId)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<Fine>> save(@Valid @RequestBody Fine fine) {
        return ResponseEntity.ok(ApiResponse.success(fineService.save(fine)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Fine>> update(@PathVariable Long id, @Valid @RequestBody Fine fine) {
        return ResponseEntity.ok(ApiResponse.success(fineService.update(id, fine)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/waive")
    public ResponseEntity<ApiResponse<Fine>> waiveFine(
            @PathVariable Long id,
            @RequestParam(required = false) String waivedBy) {
        return ResponseEntity.ok(ApiResponse.success(fineService.waiveFine(id, waivedBy)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        fineService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
