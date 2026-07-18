package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentNumber(String paymentNumber);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByStudent_Id(Long studentId);
    Page<Payment> findByStudent_Id(Long studentId, Pageable pageable);
    List<Payment> findByInvoice_Id(Long invoiceId);
    List<Payment> findByPaymentStatus(String status);
    Page<Payment> findByPaymentStatus(String status, Pageable pageable);
    long countByPaymentStatus(String status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = 'COMPLETED'")
    Double sumCompletedPayments();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND FUNCTION('DATE', p.paymentDate) = CURRENT_DATE")
    Double sumTodayPayments();

    @Query("SELECT p FROM Payment p WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(p.paymentNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.student.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.student.lastName) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR p.paymentStatus = :status)")
    Page<Payment> search(@Param("search") String search, @Param("status") String status, Pageable pageable);
}
