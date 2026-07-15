package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeTypeRepository extends JpaRepository<FeeType, Long> {
}
