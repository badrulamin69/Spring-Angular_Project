package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.HostelAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostelAllocationRepository extends JpaRepository<HostelAllocation, Long> {
}
