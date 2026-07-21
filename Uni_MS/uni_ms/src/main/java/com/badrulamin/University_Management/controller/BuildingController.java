package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.payload.request.BuildingRequest;
import com.badrulamin.University_Management.payload.response.BuildingResponse;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.BuildingService;
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
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<BuildingResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BuildingResponse> paged = buildingService.findAll(pageable);
        PagedResponse<BuildingResponse> pagedResponse = new PagedResponse<>(
                paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(),
                paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    public ResponseEntity<ApiResponse<BuildingResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(buildingService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    public ResponseEntity<ApiResponse<BuildingResponse>> create(@Valid @RequestBody BuildingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(buildingService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    public ResponseEntity<ApiResponse<BuildingResponse>> update(@PathVariable Long id, @Valid @RequestBody BuildingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(buildingService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        buildingService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    public ResponseEntity<ApiResponse<List<BuildingResponse>>> findActive() {
        return ResponseEntity.ok(ApiResponse.success(buildingService.findActive()));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    public ResponseEntity<ApiResponse<List<BuildingResponse>>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(ApiResponse.success(buildingService.search(search, isActive)));
    }
}
