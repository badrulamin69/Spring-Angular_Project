package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.FeeStructure;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.FeeStructureService;
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
@RequestMapping("/api/fee-structures")
@RequiredArgsConstructor
public class FeeStructureController {

    private final FeeStructureService feeStructureService;

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<FeeStructure>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FeeStructure> paged = feeStructureService.findAll(pageable);
        PagedResponse<FeeStructure> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeeStructure>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(feeStructureService.findById(id)));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/semester/{semesterId}/program/{programId}")
    public ResponseEntity<ApiResponse<List<FeeStructure>>> findBySemesterAndProgram(@PathVariable Long semesterId, @PathVariable Long programId) {
        return ResponseEntity.ok(ApiResponse.success(feeStructureService.findBySemesterAndProgram(semesterId, programId)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<FeeStructure>> save(@Valid @RequestBody FeeStructure feeStructure) {
        return ResponseEntity.ok(ApiResponse.success(feeStructureService.save(feeStructure)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FeeStructure>> update(@PathVariable Long id, @Valid @RequestBody FeeStructure feeStructure) {
        return ResponseEntity.ok(ApiResponse.success(feeStructureService.update(id, feeStructure)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        feeStructureService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
