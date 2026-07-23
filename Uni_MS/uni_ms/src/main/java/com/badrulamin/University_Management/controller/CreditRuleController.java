package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.CreditRule;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.CreditRuleService;
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
@RequestMapping("/api/credit-rules")
@RequiredArgsConstructor
public class CreditRuleController {

    private final CreditRuleService creditRuleService;

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CreditRule>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CreditRule> paged = creditRuleService.findAll(pageable);
        PagedResponse<CreditRule> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditRule>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(creditRuleService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<CreditRule>> save(@Valid @RequestBody CreditRule creditRule) {
        return ResponseEntity.ok(ApiResponse.success(creditRuleService.save(creditRule)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditRule>> update(@PathVariable Long id, @Valid @RequestBody CreditRule creditRule) {
        return ResponseEntity.ok(ApiResponse.success(creditRuleService.update(id, creditRule)));
    }

    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        creditRuleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
