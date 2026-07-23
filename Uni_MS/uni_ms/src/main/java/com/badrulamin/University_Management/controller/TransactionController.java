package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Transaction;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.TransactionService;
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
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<Transaction>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Transaction> paged = transactionService.findAll(pageable);
        PagedResponse<Transaction> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    public ResponseEntity<ApiResponse<Transaction>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    public ResponseEntity<ApiResponse<Transaction>> save(@Valid @RequestBody Transaction transaction) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.save(transaction)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    public ResponseEntity<ApiResponse<Transaction>> update(@PathVariable Long id, @Valid @RequestBody Transaction transaction) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.update(id, transaction)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
