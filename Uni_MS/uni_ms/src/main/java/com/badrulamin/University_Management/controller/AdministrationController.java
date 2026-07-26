package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Administration;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.AdministrationService;
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
@RequestMapping("/api/administrations")
@RequiredArgsConstructor
public class AdministrationController {

    private final AdministrationService administrationService;

    @PreAuthorize("hasAuthority('ADMINISTRATION_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Administration>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Administration> paged = administrationService.findAll(pageable);
        PagedResponse<Administration> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ADMINISTRATION_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Administration>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(administrationService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ADMINISTRATION_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<Administration>> save(@Valid @RequestBody Administration administration) {
        return ResponseEntity.ok(ApiResponse.success(administrationService.save(administration)));
    }

    @PreAuthorize("hasAuthority('ADMINISTRATION_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Administration>> update(@PathVariable Long id, @Valid @RequestBody Administration administration) {
        return ResponseEntity.ok(ApiResponse.success(administrationService.update(id, administration)));
    }

    @PreAuthorize("hasAuthority('ADMINISTRATION_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        administrationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
