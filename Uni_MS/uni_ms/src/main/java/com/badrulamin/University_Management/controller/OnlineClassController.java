package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.OnlineClass;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.OnlineClassService;
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
@RequestMapping("/api/online-classes")
@RequiredArgsConstructor
public class OnlineClassController {

    private final OnlineClassService onlineClassService;

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OnlineClass>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OnlineClass> paged = onlineClassService.findAll(pageable);
        PagedResponse<OnlineClass> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OnlineClass>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(onlineClassService.findById(id)));
    }

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @PostMapping
    public ResponseEntity<ApiResponse<OnlineClass>> save(@Valid @RequestBody OnlineClass onlineClass) {
        return ResponseEntity.ok(ApiResponse.success(onlineClassService.save(onlineClass)));
    }

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OnlineClass>> update(@PathVariable Long id, @Valid @RequestBody OnlineClass onlineClass) {
        return ResponseEntity.ok(ApiResponse.success(onlineClassService.update(id, onlineClass)));
    }

    @PreAuthorize("hasAuthority('LMS_VIEW')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        onlineClassService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
