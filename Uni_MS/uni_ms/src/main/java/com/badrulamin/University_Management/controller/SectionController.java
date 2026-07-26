package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Section;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.SectionService;
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
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @GetMapping
    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<Section>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Section> paged = sectionService.findAll(pageable);
        PagedResponse<Section> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    public ResponseEntity<ApiResponse<Section>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(sectionService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    public ResponseEntity<ApiResponse<Section>> save(@Valid @RequestBody Section section) {
        return ResponseEntity.ok(ApiResponse.success(sectionService.save(section)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    public ResponseEntity<ApiResponse<Section>> update(@PathVariable Long id, @Valid @RequestBody Section section) {
        return ResponseEntity.ok(ApiResponse.success(sectionService.update(id, section)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        sectionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
