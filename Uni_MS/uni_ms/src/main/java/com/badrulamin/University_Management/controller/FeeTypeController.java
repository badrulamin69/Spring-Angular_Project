package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.FeeType;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.FeeTypeService;
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
@RequestMapping("/api/fee-types")
@RequiredArgsConstructor
public class FeeTypeController {

    private final FeeTypeService feeTypeService;

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<FeeType>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FeeType> paged = feeTypeService.findAll(pageable);
        PagedResponse<FeeType> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<FeeType> findById(@PathVariable Long id) {
        return ResponseEntity.ok(feeTypeService.findById(id));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/active")
    public ResponseEntity<List<FeeType>> findActive() {
        return ResponseEntity.ok(feeTypeService.findActive());
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<FeeType>> findByCategory(@PathVariable String category) {
        return ResponseEntity.ok(feeTypeService.findByCategory(category));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping
    public ResponseEntity<FeeType> save(@Valid @RequestBody FeeType feeType) {
        return ResponseEntity.ok(feeTypeService.save(feeType));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<FeeType> update(@PathVariable Long id, @Valid @RequestBody FeeType feeType) {
        return ResponseEntity.ok(feeTypeService.update(id, feeType));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feeTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
