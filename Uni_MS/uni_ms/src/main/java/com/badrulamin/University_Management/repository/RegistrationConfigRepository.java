package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.RegistrationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationConfigRepository extends JpaRepository<RegistrationConfig, Long> {

    Optional<RegistrationConfig> findBySemester_IdAndIsActiveTrue(Long semesterId);

    List<RegistrationConfig> findByIsActiveTrue();

    List<RegistrationConfig> findByIsClosedFalse();

    @Query("SELECT rc FROM RegistrationConfig rc WHERE rc.semester.id = :semesterId AND rc.startDate <= :date AND rc.endDate >= :date AND rc.isActive = true AND rc.isClosed = false")
    Optional<RegistrationConfig> findActiveConfig(@Param("semesterId") Long semesterId, @Param("date") LocalDate date);

    boolean existsBySemester_IdAndIsActiveTrue(Long semesterId);
}
