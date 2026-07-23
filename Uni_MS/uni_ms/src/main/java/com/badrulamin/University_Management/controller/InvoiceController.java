package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Invoice;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Invoice>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Invoice> paged = invoiceService.findAll(pageable);
        PagedResponse<Invoice> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<Invoice>>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Invoice> paged = invoiceService.search(search, status, pageable);
        PagedResponse<Invoice> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Invoice>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.findById(id)));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<PagedResponse<Invoice>>> findByStudentId(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Invoice> paged = invoiceService.findByStudentId(studentId, pageable);
        PagedResponse<Invoice> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Invoice>> generateInvoice(
            @RequestParam Long studentId,
            @RequestParam Long semesterId,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.generateInvoice(studentId, semesterId, academicYear)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Invoice>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.updateStatus(id, status)));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
