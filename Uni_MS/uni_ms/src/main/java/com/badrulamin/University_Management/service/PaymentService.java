package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Payment;
import com.badrulamin.University_Management.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Page<Payment> findAll(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment update(Long id, Payment payment) {
        findById(id);
        payment.setId(id);
        return paymentRepository.save(payment);
    }

    public void delete(Long id) {
        findById(id);
        paymentRepository.deleteById(id);
    }
}
