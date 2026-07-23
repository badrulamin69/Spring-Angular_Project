package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.repository.*;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboards/accounts-officer")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ACCOUNTS_OFFICER')")
public class AccountsDashboardController {

    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRevenue", transactionRepository.count());
        stats.put("pendingPayments", paymentRepository.count());
        stats.put("totalInvoices", invoiceRepository.count());
        stats.put("recentTransactions", transactionRepository.count());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
