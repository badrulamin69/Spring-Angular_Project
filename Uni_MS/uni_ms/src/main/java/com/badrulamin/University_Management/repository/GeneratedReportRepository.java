package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.GeneratedReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratedReportRepository extends JpaRepository<GeneratedReport, Long> {
}
