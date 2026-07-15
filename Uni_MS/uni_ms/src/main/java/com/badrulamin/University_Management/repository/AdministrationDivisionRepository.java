package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdministrationDivision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministrationDivisionRepository extends JpaRepository<AdministrationDivision, Long> {
    Optional<AdministrationDivision> findByCode(String code);
    boolean existsByCode(String code);
    List<AdministrationDivision> findByCampus_Id(Long campusId);
}
