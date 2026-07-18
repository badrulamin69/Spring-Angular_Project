package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {
    List<Fine> findByStudent_Id(Long studentId);
    List<Fine> findByStatus(String status);
    long countByStatus(String status);
    List<Fine> findByStudent_IdAndStatus(Long studentId, String status);
}
