package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Account;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.AccountService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Account>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Account> paged = accountService.findAll(pageable);
        PagedResponse<Account> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    public ResponseEntity<ApiResponse<Account>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(accountService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVOICE_MANAGE')")
    public ResponseEntity<ApiResponse<Account>> save(@Valid @RequestBody Account account) {
        return ResponseEntity.ok(ApiResponse.success(accountService.save(account)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVOICE_MANAGE')")
    public ResponseEntity<ApiResponse<Account>> update(@PathVariable Long id, @Valid @RequestBody Account account) {
        return ResponseEntity.ok(ApiResponse.success(accountService.update(id, account)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INVOICE_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
