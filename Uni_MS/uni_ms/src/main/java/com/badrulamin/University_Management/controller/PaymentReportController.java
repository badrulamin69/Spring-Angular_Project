package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.service.PaymentReportService;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/payment-reports")
@RequiredArgsConstructor
public class PaymentReportController {

    private final PaymentReportService paymentReportService;

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return ResponseEntity.ok(ApiResponse.success(paymentReportService.getDailyReport(date)));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyReport(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(ApiResponse.success(paymentReportService.getMonthlyReport(month, year)));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/yearly")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getYearlyReport(
            @RequestParam int year) {
        return ResponseEntity.ok(ApiResponse.success(paymentReportService.getYearlyReport(year)));
    }

    @PreAuthorize("hasAuthority('FINANCE_VIEW')")
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPaymentAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(paymentReportService.getPaymentAnalytics()));
    }
}
