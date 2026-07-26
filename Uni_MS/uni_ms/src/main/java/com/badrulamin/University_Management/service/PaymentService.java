package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Invoice;
import com.badrulamin.University_Management.entity.Payment;
import com.badrulamin.University_Management.entity.PaymentTransaction;
import com.badrulamin.University_Management.entity.Refund;
import com.badrulamin.University_Management.entity.Student;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.response.PaymentResponse;
import com.badrulamin.University_Management.repository.InvoiceRepository;
import com.badrulamin.University_Management.repository.PaymentRepository;
import com.badrulamin.University_Management.repository.PaymentTransactionRepository;
import com.badrulamin.University_Management.repository.RefundRepository;
import com.badrulamin.University_Management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final InvoiceRepository invoiceRepository;
    private final StudentRepository studentRepository;
    private final RefundRepository refundRepository;

    public Page<Payment> findAll(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    public Page<Payment> search(String search, String status, Pageable pageable) {
        return paymentRepository.search(search, status, pageable);
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }

    public Page<Payment> findByStudentId(Long studentId, Pageable pageable) {
        return paymentRepository.findByStudent_Id(studentId, pageable);
    }

    public List<Payment> findByInvoiceId(Long invoiceId) {
        return paymentRepository.findByInvoice_Id(invoiceId);
    }

    public Payment initiatePayment(Long invoiceId, Long studentId, Double amount, String paymentMethod, String notes) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        if ("PAID".equals(invoice.getStatus())) {
            throw new BusinessException("Invoice is already fully paid");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        Payment payment = new Payment();
        payment.setPaymentNumber(generatePaymentNumber());
        payment.setInvoice(invoice);
        payment.setStudent(student);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("PENDING");
        payment.setNotes(notes);
        return paymentRepository.save(payment);
    }

    public Payment processOnlinePayment(Long paymentId, String transactionId, String gatewayResponse) {
        Payment payment = findById(paymentId);

        if (!"PENDING".equals(payment.getPaymentStatus())) {
            throw new BusinessException("Payment must be PENDING to process. Current status: " + payment.getPaymentStatus());
        }

        payment.setPaymentStatus("COMPLETED");
        payment.setTransactionId(transactionId);
        payment.setGatewayResponse(gatewayResponse);
        payment.setPaymentDate(LocalDateTime.now());

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPayment(payment);
        transaction.setTransactionId(transactionId != null ? transactionId : "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        transaction.setGatewayTransactionId(transactionId);
        transaction.setAmount(payment.getAmount());
        transaction.setStatus("SUCCESS");
        transaction.setGatewayResponse(gatewayResponse);
        paymentTransactionRepository.save(transaction);

        updateInvoiceAmounts(payment.getInvoice(), payment.getAmount());

        return paymentRepository.save(payment);
    }

    public Payment processOfflinePayment(Long paymentId) {
        Payment payment = findById(paymentId);
        payment.setPaymentStatus("PENDING");
        return paymentRepository.save(payment);
    }

    public Payment approvePayment(Long paymentId, String approvedBy) {
        Payment payment = findById(paymentId);
        payment.setPaymentStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPayment(payment);
        transaction.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        transaction.setAmount(payment.getAmount());
        transaction.setStatus("SUCCESS");
        paymentTransactionRepository.save(transaction);

        updateInvoiceAmounts(payment.getInvoice(), payment.getAmount());

        return paymentRepository.save(payment);
    }

    public Payment rejectPayment(Long paymentId) {
        Payment payment = findById(paymentId);
        payment.setPaymentStatus("FAILED");
        return paymentRepository.save(payment);
    }

    public Refund refundPayment(Long paymentId, Double amount, String reason, String approvedBy) {
        Payment payment = findById(paymentId);

        Refund refund = new Refund();
        refund.setRefundNumber(generateRefundNumber());
        refund.setPayment(payment);
        refund.setStudent(payment.getStudent());
        refund.setAmount(amount);
        refund.setReason(reason);
        refund.setStatus("COMPLETED");
        refund.setApprovedBy(approvedBy);
        refund.setApprovedAt(LocalDateTime.now());
        refund = refundRepository.save(refund);

        payment.setPaymentStatus("REFUNDED");
        paymentRepository.save(payment);

        Invoice invoice = payment.getInvoice();
        BigDecimal refundAmt = BigDecimal.valueOf(amount);
        BigDecimal newPaidAmount = BigDecimal.valueOf(invoice.getPaidAmount()).subtract(refundAmt).max(BigDecimal.ZERO);
        BigDecimal newDueAmount = BigDecimal.valueOf(invoice.getDueAmount()).add(refundAmt);
        invoice.setPaidAmount(newPaidAmount.doubleValue());
        invoice.setDueAmount(newDueAmount.doubleValue());
        if (newPaidAmount.compareTo(BigDecimal.valueOf(invoice.getTotalAmount())) < 0) {
            invoice.setStatus("PARTIAL");
        }
        invoiceRepository.save(invoice);

        return refund;
    }

    public Map<String, Object> getPaymentStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPayments", paymentRepository.count());
        stats.put("completedPayments", paymentRepository.countByPaymentStatus("COMPLETED"));
        stats.put("totalAmount", paymentRepository.sumCompletedPayments());
        stats.put("todayAmount", paymentRepository.sumTodayPayments());
        stats.put("pendingPayments", paymentRepository.countByPaymentStatus("PENDING"));
        stats.put("failedPayments", paymentRepository.countByPaymentStatus("FAILED"));
        return stats;
    }

    public PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentNumber(payment.getPaymentNumber());
        Invoice invoice = payment.getInvoice();
        response.setInvoiceId(invoice != null ? invoice.getId() : null);
        Student student = payment.getStudent();
        response.setStudentId(student != null ? student.getId() : null);
        if (student != null) {
            response.setStudentName(student.getFirstName() + " " + student.getLastName());
        }
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setPaymentDate(payment.getPaymentDate());
        response.setNotes(payment.getNotes());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }

    private void updateInvoiceAmounts(Invoice invoice, Double paymentAmount) {
        BigDecimal paidAmt = BigDecimal.valueOf(invoice.getPaidAmount()).add(BigDecimal.valueOf(paymentAmount));
        BigDecimal totalAmt = BigDecimal.valueOf(invoice.getTotalAmount());
        invoice.setPaidAmount(paidAmt.doubleValue());
        invoice.setDueAmount(totalAmt.subtract(paidAmt).max(BigDecimal.ZERO).doubleValue());
        if (paidAmt.compareTo(totalAmt) >= 0) {
            invoice.setStatus("PAID");
        } else {
            invoice.setStatus("PARTIAL");
        }
        invoiceRepository.save(invoice);
    }

    private String generatePaymentNumber() {
        String prefix = "PAY-" + Year.now().getValue() + "-";
        Optional<String> maxNumber = paymentRepository.findMaxPaymentNumberByPrefix(prefix);

        long nextSeq = 1;
        if (maxNumber.isPresent()) {
            try {
                String seqPart = maxNumber.get().substring(prefix.length());
                nextSeq = Long.parseLong(seqPart) + 1;
            } catch (NumberFormatException ignored) {
            }
        }

        return prefix + String.format("%06d", nextSeq);
    }

    private String generateRefundNumber() {
        String prefix = "REF-" + Year.now().getValue() + "-";
        Optional<String> maxNumber = refundRepository.findMaxRefundNumberByPrefix(prefix);

        long nextSeq = 1;
        if (maxNumber.isPresent()) {
            try {
                String seqPart = maxNumber.get().substring(prefix.length());
                nextSeq = Long.parseLong(seqPart) + 1;
            } catch (NumberFormatException ignored) {
            }
        }

        return prefix + String.format("%06d", nextSeq);
    }
}
