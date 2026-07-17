package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Payment;
import com.badrulamin.University_Management.entity.Refund;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<Payment>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> paged = paymentService.findAll(pageable);
        PagedResponse<Payment> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<Payment>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> paged = paymentService.search(search, status, pageable);
        PagedResponse<Payment> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<Payment> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<PagedResponse<Payment>> findByStudentId(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> paged = paymentService.findByStudentId(studentId, pageable);
        PagedResponse<Payment> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<Payment>> findByInvoiceId(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(paymentService.findByInvoiceId(invoiceId));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPaymentStats() {
        return ResponseEntity.ok(paymentService.getPaymentStats());
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/initiate")
    public ResponseEntity<Payment> initiatePayment(
            @RequestParam Long invoiceId,
            @RequestParam Long studentId,
            @RequestParam Double amount,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(paymentService.initiatePayment(invoiceId, studentId, amount, paymentMethod, notes));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/process-online")
    public ResponseEntity<Payment> processOnlinePayment(
            @PathVariable Long id,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String gatewayResponse) {
        return ResponseEntity.ok(paymentService.processOnlinePayment(id, transactionId, gatewayResponse));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/process-offline")
    public ResponseEntity<Payment> processOfflinePayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.processOfflinePayment(id));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Payment> approvePayment(
            @PathVariable Long id,
            @RequestParam(required = false) String approvedBy) {
        return ResponseEntity.ok(paymentService.approvePayment(id, approvedBy));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<Payment> rejectPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.rejectPayment(id));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/refund")
    public ResponseEntity<Refund> refundPayment(
            @PathVariable Long id,
            @RequestParam Double amount,
            @RequestParam String reason,
            @RequestParam(required = false) String approvedBy) {
        return ResponseEntity.ok(paymentService.refundPayment(id, amount, reason, approvedBy));
    }
}
