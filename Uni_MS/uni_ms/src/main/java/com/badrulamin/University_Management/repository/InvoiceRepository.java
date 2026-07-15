package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i WHERE " +
           "LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.student.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.student.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Invoice> searchInvoices(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:studentId IS NULL OR i.student.id = :studentId)")
    Page<Invoice> findAllWithFilters(@Param("status") String status,
                                     @Param("studentId") Long studentId,
                                     Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE " +
           "(LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.student.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.student.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:studentId IS NULL OR i.student.id = :studentId)")
    Page<Invoice> searchInvoicesWithFilters(@Param("keyword") String keyword,
                                            @Param("status") String status,
                                            @Param("studentId") Long studentId,
                                            Pageable pageable);
}
