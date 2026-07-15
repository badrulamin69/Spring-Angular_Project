package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.CreditRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditRuleRepository extends JpaRepository<CreditRule, Long> {
    Optional<CreditRule> findByProgram_Id(Long programId);
}
