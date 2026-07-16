package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.DepartmentAllocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentAllocationRepository extends JpaRepository<DepartmentAllocation, Long> {
    Optional<DepartmentAllocation> findByAllocationNumber(String allocationNumber);
    Optional<DepartmentAllocation> findByRegistration_Id(Long registrationId);
    List<DepartmentAllocation> findByStatus(String status);
    Page<DepartmentAllocation> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);
    List<DepartmentAllocation> findByAllocatedProgram_IdOrderByMeritRankAsc(Long programId);
}
