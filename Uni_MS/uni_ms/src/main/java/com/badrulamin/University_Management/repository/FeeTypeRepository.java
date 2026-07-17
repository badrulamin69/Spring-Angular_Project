package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeTypeRepository extends JpaRepository<FeeType, Long> {
    Optional<FeeType> findByCode(String code);
    Optional<FeeType> findByName(String name);
    List<FeeType> findByIsActiveTrue();
    List<FeeType> findByCategory(String category);
    boolean existsByCode(String code);
    boolean existsByName(String name);
}
