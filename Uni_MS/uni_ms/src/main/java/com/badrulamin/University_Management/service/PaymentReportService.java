package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Payment;
import com.badrulamin.University_Management.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentReportService {

    private final PaymentRepository paymentRepository;

    public Map<String, Object> getDailyReport(LocalDate date) {
        Map<String, Object> report = new HashMap<>();
        List<Payment> allPayments = paymentRepository.findAll();

        double total = 0.0;
        int count = 0;
        Map<String, Double> byMethod = new HashMap<>();

        for (Payment p : allPayments) {
            if ("COMPLETED".equals(p.getPaymentStatus())
                    && p.getPaymentDate() != null
                    && p.getPaymentDate().toLocalDate().equals(date)) {
                total += p.getAmount();
                count++;
                byMethod.merge(p.getPaymentMethod() != null ? p.getPaymentMethod() : "UNKNOWN", p.getAmount(), Double::sum);
            }
        }

        report.put("date", date.toString());
        report.put("total", total);
        report.put("count", count);
        report.put("byMethod", byMethod);
        return report;
    }

    public Map<String, Object> getMonthlyReport(int month, int year) {
        Map<String, Object> report = new HashMap<>();
        List<Payment> allPayments = paymentRepository.findAll();

        double total = 0.0;
        int count = 0;
        Map<String, Double> byMethod = new HashMap<>();

        for (Payment p : allPayments) {
            if ("COMPLETED".equals(p.getPaymentStatus())
                    && p.getPaymentDate() != null
                    && p.getPaymentDate().getMonthValue() == month
                    && p.getPaymentDate().getYear() == year) {
                total += p.getAmount();
                count++;
                byMethod.merge(p.getPaymentMethod() != null ? p.getPaymentMethod() : "UNKNOWN", p.getAmount(), Double::sum);
            }
        }

        report.put("month", month);
        report.put("year", year);
        report.put("total", total);
        report.put("count", count);
        report.put("byMethod", byMethod);
        return report;
    }

    public Map<String, Object> getYearlyReport(int year) {
        Map<String, Object> report = new HashMap<>();
        List<Payment> allPayments = paymentRepository.findAll();

        double total = 0.0;
        int count = 0;
        Map<String, Double> byMethod = new HashMap<>();

        for (Payment p : allPayments) {
            if ("COMPLETED".equals(p.getPaymentStatus())
                    && p.getPaymentDate() != null
                    && p.getPaymentDate().getYear() == year) {
                total += p.getAmount();
                count++;
                byMethod.merge(p.getPaymentMethod() != null ? p.getPaymentMethod() : "UNKNOWN", p.getAmount(), Double::sum);
            }
        }

        report.put("year", year);
        report.put("total", total);
        report.put("count", count);
        report.put("byMethod", byMethod);
        return report;
    }

    public Map<String, Object> getPaymentAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        Map<String, Long> statusCounts = new HashMap<>();
        statusCounts.put("COMPLETED", paymentRepository.countByPaymentStatus("COMPLETED"));
        statusCounts.put("PENDING", paymentRepository.countByPaymentStatus("PENDING"));
        statusCounts.put("FAILED", paymentRepository.countByPaymentStatus("FAILED"));

        List<Payment> allPayments = paymentRepository.findAll();
        Map<String, Long> methodCounts = new HashMap<>();
        Map<String, Map<String, Object>> monthlyTrend = new HashMap<>();

        for (Payment p : allPayments) {
            String method = p.getPaymentMethod() != null ? p.getPaymentMethod() : "UNKNOWN";
            methodCounts.merge(method, 1L, Long::sum);

            if ("COMPLETED".equals(p.getPaymentStatus()) && p.getPaymentDate() != null) {
                String monthKey = String.format("%d-%02d", p.getPaymentDate().getYear(), p.getPaymentDate().getMonthValue());
                monthlyTrend.computeIfAbsent(monthKey, k -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("total", 0.0);
                    m.put("count", 0L);
                    return m;
                });
                Map<String, Object> monthData = monthlyTrend.get(monthKey);
                monthData.put("total", (Double) monthData.get("total") + p.getAmount());
                monthData.put("count", (Long) monthData.get("count") + 1);
            }
        }

        analytics.put("statusCounts", statusCounts);
        analytics.put("methodCounts", methodCounts);
        analytics.put("monthlyTrend", monthlyTrend);
        analytics.put("totalAmount", paymentRepository.sumCompletedPayments());
        analytics.put("todayAmount", paymentRepository.sumTodayPayments());
        return analytics;
    }
}