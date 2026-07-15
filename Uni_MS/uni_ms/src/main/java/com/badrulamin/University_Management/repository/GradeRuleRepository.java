package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.GradeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRuleRepository extends JpaRepository<GradeRule, Long> {
}
