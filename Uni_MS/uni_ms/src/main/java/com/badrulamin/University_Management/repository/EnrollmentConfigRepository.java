package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.EnrollmentConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentConfigRepository extends JpaRepository<EnrollmentConfig, Long> {

    Optional<EnrollmentConfig> findBySemester_IdAndIsActiveTrue(Long semesterId);

    List<EnrollmentConfig> findByIsActiveTrue();

    @Query("SELECT ec FROM EnrollmentConfig ec WHERE ec.semester.id = :semesterId AND ec.startDate <= :date AND ec.endDate >= :date AND ec.isActive = true AND ec.isClosed = false")
    Optional<EnrollmentConfig> findActiveConfig(@Param("semesterId") Long semesterId, @Param("date") LocalDate date);

    boolean existsBySemester_IdAndIsActiveTrue(Long semesterId);
}
