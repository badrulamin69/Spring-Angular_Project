package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Invoice;
import com.badrulamin.University_Management.entity.Payment;
import com.badrulamin.University_Management.entity.Student;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private RefundRepository refundRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void initiatePayment_validInputs_returnsPayment() {
        Long invoiceId = 1L;
        Long studentId = 1L;

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setStatus("PENDING");
        invoice.setTotalAmount(1000.0);
        invoice.setPaidAmount(0.0);
        invoice.setDueAmount(1000.0);

        Student student = new Student();
        student.setId(studentId);
        student.setFirstName("John");
        student.setLastName("Doe");

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        Payment result = paymentService.initiatePayment(invoiceId, studentId, 500.0, "BANK_TRANSFER", "Test payment");

        assertEquals("PENDING", result.getPaymentStatus());
        assertEquals(500.0, result.getAmount());
        assertEquals("BANK_TRANSFER", result.getPaymentMethod());
        assertNotNull(result.getPaymentNumber());
        assertTrue(result.getPaymentNumber().startsWith("PAY-"));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void initiatePayment_alreadyPaidInvoice_throwsBusinessException() {
        Long invoiceId = 1L;

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setStatus("PAID");

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> paymentService.initiatePayment(invoiceId, 1L, 500.0, "BANK_TRANSFER", null));

        assertTrue(ex.getMessage().contains("already fully paid"));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void initiatePayment_invoiceNotFound_throwsException() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.initiatePayment(999L, 1L, 500.0, "CASH", null));
    }

    @Test
    void initiatePayment_studentNotFound_throwsException() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setStatus("PENDING");

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.initiatePayment(1L, 999L, 500.0, "CASH", null));
    }

    @Test
    void findById_existingPayment_returnsPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaymentNumber("PAY-2026-000001");
        payment.setPaymentStatus("PENDING");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        Payment result = paymentService.findById(1L);

        assertEquals("PAY-2026-000001", result.getPaymentNumber());
        assertEquals("PENDING", result.getPaymentStatus());
    }

    @Test
    void findById_nonExisting_throwsException() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.findById(999L));
    }

    @Test
    void processOfflinePayment_pendingPayment_returnsPending() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaymentStatus("PENDING");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.processOfflinePayment(1L);

        assertEquals("PENDING", result.getPaymentStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    void rejectPayment_existingPayment_returnsFailed() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaymentStatus("PENDING");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.rejectPayment(1L);

        assertEquals("FAILED", result.getPaymentStatus());
    }

    @Test
    void getPaymentStats_returnsStats() {
        when(paymentRepository.count()).thenReturn(10L);
        when(paymentRepository.countByPaymentStatus("COMPLETED")).thenReturn(7L);
        when(paymentRepository.sumCompletedPayments()).thenReturn(5000.0);
        when(paymentRepository.sumTodayPayments()).thenReturn(500.0);
        when(paymentRepository.countByPaymentStatus("PENDING")).thenReturn(2L);
        when(paymentRepository.countByPaymentStatus("FAILED")).thenReturn(1L);

        var stats = paymentService.getPaymentStats();

        assertEquals(10L, stats.get("totalPayments"));
        assertEquals(7L, stats.get("completedPayments"));
        assertEquals(5000.0, stats.get("totalAmount"));
        assertEquals(500.0, stats.get("todayAmount"));
        assertEquals(2L, stats.get("pendingPayments"));
        assertEquals(1L, stats.get("failedPayments"));
    }
}
