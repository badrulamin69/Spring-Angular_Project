package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.DisciplinaryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplinaryRecordRepository extends JpaRepository<DisciplinaryRecord, Long> {
    Page<DisciplinaryRecord> findByStudentId(Long studentId, Pageable pageable);
    long countByStatus(String status);
    long countBySeverity(String severity);
}
