package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmissionTestRepository extends JpaRepository<AdmissionTest, Long> {
}
