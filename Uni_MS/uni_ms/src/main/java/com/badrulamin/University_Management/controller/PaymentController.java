package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Payment;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.entity.Refund;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.payload.response.PaymentResponse;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> paged = paymentService.findAll(pageable);
        Page<PaymentResponse> dtoPage = paged.map(paymentService::toResponse);
        PagedResponse<PaymentResponse> response = new PagedResponse<>(dtoPage.getContent(), dtoPage.getNumber(), dtoPage.getSize(), dtoPage.getTotalElements(), dtoPage.getTotalPages(), dtoPage.isFirst(), dtoPage.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> paged = paymentService.search(search, status, pageable);
        Page<PaymentResponse> dtoPage = paged.map(paymentService::toResponse);
        PagedResponse<PaymentResponse> response = new PagedResponse<>(dtoPage.getContent(), dtoPage.getNumber(), dtoPage.getSize(), dtoPage.getTotalElements(), dtoPage.getTotalPages(), dtoPage.isFirst(), dtoPage.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.toResponse(paymentService.findById(id))));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> findByStudentId(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> paged = paymentService.findByStudentId(studentId, pageable);
        Page<PaymentResponse> dtoPage = paged.map(paymentService::toResponse);
        PagedResponse<PaymentResponse> response = new PagedResponse<>(dtoPage.getContent(), dtoPage.getNumber(), dtoPage.getSize(), dtoPage.getTotalElements(), dtoPage.getTotalPages(), dtoPage.isFirst(), dtoPage.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> findByInvoiceId(@PathVariable Long invoiceId) {
        List<PaymentResponse> responses = paymentService.findByInvoiceId(invoiceId).stream()
                .map(paymentService::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPaymentStats() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentStats()));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @RequestParam Long invoiceId,
            @RequestParam Long studentId,
            @RequestParam Double amount,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.toResponse(paymentService.initiatePayment(invoiceId, studentId, amount, paymentMethod, notes))));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/process-online")
    public ResponseEntity<ApiResponse<PaymentResponse>> processOnlinePayment(
            @PathVariable Long id,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String gatewayResponse) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.toResponse(paymentService.processOnlinePayment(id, transactionId, gatewayResponse))));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/process-offline")
    public ResponseEntity<ApiResponse<PaymentResponse>> processOfflinePayment(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.toResponse(paymentService.processOfflinePayment(id))));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<PaymentResponse>> approvePayment(
            @PathVariable Long id,
            @RequestParam(required = false) String approvedBy) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.toResponse(paymentService.approvePayment(id, approvedBy))));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<PaymentResponse>> rejectPayment(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.toResponse(paymentService.rejectPayment(id))));
    }

    @PreAuthorize("hasAuthority('FINANCE_MANAGE')")
    @PostMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<Refund>> refundPayment(
            @PathVariable Long id,
            @RequestParam Double amount,
            @RequestParam String reason,
            @RequestParam(required = false) String approvedBy) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.refundPayment(id, amount, reason, approvedBy)));
    }
}
