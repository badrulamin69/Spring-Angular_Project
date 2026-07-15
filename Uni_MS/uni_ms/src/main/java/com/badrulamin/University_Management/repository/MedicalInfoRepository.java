package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.MedicalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalInfoRepository extends JpaRepository<MedicalInfo, Long> {
    Optional<MedicalInfo> findByStudent_Id(Long studentId);
    boolean existsByStudent_Id(Long studentId);
}
