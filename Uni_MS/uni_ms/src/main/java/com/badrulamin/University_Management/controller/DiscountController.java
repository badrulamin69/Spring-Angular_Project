package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Discount;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.DiscountService;
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
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Discount>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Discount> paged = discountService.findAll(pageable);
        PagedResponse<Discount> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Discount>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(discountService.findById(id)));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Discount>>> findByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(discountService.findByStudentId(studentId)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<Discount>> save(@Valid @RequestBody Discount discount) {
        return ResponseEntity.ok(ApiResponse.success(discountService.save(discount)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Discount>> update(@PathVariable Long id, @Valid @RequestBody Discount discount) {
        return ResponseEntity.ok(ApiResponse.success(discountService.update(id, discount)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        discountService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
