package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query("SELECT e FROM Exam e WHERE " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.course.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Exam> searchExams(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE " +
           "(:courseId IS NULL OR e.course.id = :courseId) AND " +
           "(:examType IS NULL OR e.examType = :examType)")
    Page<Exam> findAllWithFilters(@Param("courseId") Long courseId,
                                  @Param("examType") String examType,
                                  Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE " +
           "(LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.course.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:courseId IS NULL OR e.course.id = :courseId) AND " +
           "(:examType IS NULL OR e.examType = :examType)")
    Page<Exam> searchExamsWithFilters(@Param("keyword") String keyword,
                                      @Param("courseId") Long courseId,
                                      @Param("examType") String examType,
                                      Pageable pageable);
}
