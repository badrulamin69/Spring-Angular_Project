package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByStudentIdAndIsActiveTrue(Long studentId);
    List<Discount> findByStudentId(Long studentId);
    List<Discount> findByFeeTypeIdAndIsActiveTrue(Long feeTypeId);
}
