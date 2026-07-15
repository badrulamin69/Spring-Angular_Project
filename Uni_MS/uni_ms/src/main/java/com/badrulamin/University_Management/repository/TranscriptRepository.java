package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Transcript;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, Long> {
    Page<Transcript> findByStudentId(Long studentId, Pageable pageable);
    Optional<Transcript> findByTranscriptNumber(String transcriptNumber);
    long countByStatus(String status);
}
