package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.StudentDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDocumentRepository extends JpaRepository<StudentDocument, Long> {
    Page<StudentDocument> findByStudentId(Long studentId, Pageable pageable);
    long countByStatus(String status);
    long countByDocumentType(String documentType);
}
