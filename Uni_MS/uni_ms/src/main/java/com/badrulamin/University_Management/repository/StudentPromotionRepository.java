package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.StudentPromotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentPromotionRepository extends JpaRepository<StudentPromotion, Long> {
    Page<StudentPromotion> findByStudent_Id(Long studentId, Pageable pageable);
    long countByStatus(String status);
}
