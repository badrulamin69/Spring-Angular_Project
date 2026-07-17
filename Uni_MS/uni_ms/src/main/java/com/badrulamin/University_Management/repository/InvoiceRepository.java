package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByStudentId(Long studentId);
    Page<Invoice> findByStudentId(Long studentId, Pageable pageable);
    List<Invoice> findByStatus(String status);
    Page<Invoice> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);

    @Query("SELECT i FROM Invoice i WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.student.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.student.lastName) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR i.status = :status)")
    Page<Invoice> search(@Param("search") String search, @Param("status") String status, Pageable pageable);
}
