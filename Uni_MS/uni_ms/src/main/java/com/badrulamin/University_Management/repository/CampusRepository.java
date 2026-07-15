package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Campus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampusRepository extends JpaRepository<Campus, Long> {
    Optional<Campus> findByCode(String code);
    boolean existsByCode(String code);
    List<Campus> findByIsActiveTrue();
}
