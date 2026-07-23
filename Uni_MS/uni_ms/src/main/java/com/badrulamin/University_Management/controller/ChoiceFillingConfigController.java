package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.ChoiceFillingConfig;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.ChoiceFillingConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/choice-filling-configs")
@RequiredArgsConstructor
public class ChoiceFillingConfigController {

    private final ChoiceFillingConfigService configService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<PagedResponse<ChoiceFillingConfig>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long sessionId) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ChoiceFillingConfig> paged = configService.findByFilters(search, status, sessionId, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<ChoiceFillingConfig>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(configService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<ChoiceFillingConfig>> create(@RequestBody ChoiceFillingConfig config) {
        return ResponseEntity.ok(ApiResponse.success(configService.save(config)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<ChoiceFillingConfig>> update(@PathVariable Long id, @RequestBody ChoiceFillingConfig config) {
        return ResponseEntity.ok(ApiResponse.success(configService.update(id, config)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        configService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<ChoiceFillingConfig>> activate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(configService.activate(id)));
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasAnyAuthority('ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<ChoiceFillingConfig>> close(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(configService.close(id)));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(configService.getStats()));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('ADMISSION_VIEW', 'ADMISSION_MANAGE')")
    public ResponseEntity<ApiResponse<ChoiceFillingConfig>> getActiveConfig() {
        try {
            return ResponseEntity.ok(ApiResponse.success(configService.findActiveConfig()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
        }
    }
}
