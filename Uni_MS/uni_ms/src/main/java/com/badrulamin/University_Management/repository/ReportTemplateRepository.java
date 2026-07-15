package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.ReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {
}
