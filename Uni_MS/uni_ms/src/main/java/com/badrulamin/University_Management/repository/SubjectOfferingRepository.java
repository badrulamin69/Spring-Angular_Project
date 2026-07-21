package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.SubjectOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectOfferingRepository extends JpaRepository<SubjectOffering, Long> {

    List<SubjectOffering> findBySemester_IdAndIsActiveTrue(Long semesterId);

    List<SubjectOffering> findBySemester_IdAndBatch_IdAndIsActiveTrue(Long semesterId, Long batchId);

    Optional<SubjectOffering> findBySubject_IdAndSemester_IdAndBatch_Id(Long subjectId, Long semesterId, Long batchId);

    @Query("SELECT so FROM SubjectOffering so WHERE so.semester.id = :semesterId AND so.batch.id = :batchId AND so.isActive = true AND so.enrolledCount < so.maxSeats")
    List<SubjectOffering> findAvailableOfferings(@Param("semesterId") Long semesterId, @Param("batchId") Long batchId);

    @Query("SELECT so FROM SubjectOffering so WHERE so.semester.id = :semesterId AND so.batch.id = :batchId AND so.isActive = true")
    List<SubjectOffering> findActiveOfferings(@Param("semesterId") Long semesterId, @Param("batchId") Long batchId);

    @Query("SELECT so FROM SubjectOffering so WHERE so.semester.id = :semesterId AND so.isActive = true AND so.dayOfWeek = :day AND so.startTime < :endTime AND so.endTime > :startTime AND so.id <> :excludeId")
    List<SubjectOffering> findConflictingOfferings(@Param("semesterId") Long semesterId, @Param("day") String day, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("excludeId") Long excludeId);

    boolean existsBySubject_IdAndSemester_IdAndBatch_IdAndIsActiveTrue(Long subjectId, Long semesterId, Long batchId);
}
