package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
