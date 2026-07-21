package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionTestQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionTestQuestionRepository extends JpaRepository<AdmissionTestQuestion, Long> {
    List<AdmissionTestQuestion> findByTest_IdOrderByCreatedAtAsc(Long testId);
    long countByTest_Id(Long testId);
    Page<AdmissionTestQuestion> findByTest_Id(Long testId, Pageable pageable);

    @Query("SELECT q FROM AdmissionTestQuestion q WHERE " +
           "(:testId IS NULL OR q.test.id = :testId) " +
           "AND (:subject IS NULL OR :subject = '' OR q.subject = :subject) " +
           "AND (:difficulty IS NULL OR :difficulty = '' OR q.difficulty = :difficulty) " +
           "AND (:search IS NULL OR :search = '' OR LOWER(q.questionText) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<AdmissionTestQuestion> findByFilters(@Param("testId") Long testId,
                                               @Param("subject") String subject,
                                               @Param("difficulty") String difficulty,
                                               @Param("search") String search,
                                               Pageable pageable);

    List<AdmissionTestQuestion> findByTest_IdAndIsActiveTrue(Long testId);
    long countByTest_IdAndIsActiveTrue(Long testId);
    long countByTest_IdAndSubject(Long testId, String subject);
    List<AdmissionTestQuestion> findBySubject(String subject);

    @Query("SELECT DISTINCT q.subject FROM AdmissionTestQuestion q WHERE q.subject IS NOT NULL ORDER BY q.subject")
    List<String> findDistinctSubjects();
}
