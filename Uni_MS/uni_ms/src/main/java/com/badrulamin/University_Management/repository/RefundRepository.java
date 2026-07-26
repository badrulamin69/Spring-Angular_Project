package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByRefundNumber(String refundNumber);
    List<Refund> findByStudent_Id(Long studentId);
    List<Refund> findByStatus(String status);
    Page<Refund> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(r.refundNumber, :prefixLength + 1) AS long)), 0) FROM Refund r WHERE r.refundNumber LIKE CONCAT(:prefix, '%')")
    Long findMaxSequenceByPrefix(@Param("prefix") String prefix, @Param("prefixLength") int prefixLength);

    @Query("SELECT MAX(r.refundNumber) FROM Refund r WHERE r.refundNumber LIKE :prefix%")
    Optional<String> findMaxRefundNumberByPrefix(@Param("prefix") String prefix);
}
