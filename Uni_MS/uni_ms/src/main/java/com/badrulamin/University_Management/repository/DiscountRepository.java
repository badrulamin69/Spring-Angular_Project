package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByStudent_IdAndIsActiveTrue(Long studentId);
    List<Discount> findByStudent_Id(Long studentId);
    List<Discount> findByFeeType_IdAndIsActiveTrue(Long feeTypeId);
}
