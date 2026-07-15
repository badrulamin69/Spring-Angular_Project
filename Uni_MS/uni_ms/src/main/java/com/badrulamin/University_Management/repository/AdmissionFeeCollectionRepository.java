package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionFeeCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmissionFeeCollectionRepository extends JpaRepository<AdmissionFeeCollection, Long> {
}
