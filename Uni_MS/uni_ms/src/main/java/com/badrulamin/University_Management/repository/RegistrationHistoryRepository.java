package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.RegistrationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationHistoryRepository extends JpaRepository<RegistrationHistory, Long> {

    Page<RegistrationHistory> findByStudent_IdOrderByCreatedAtDesc(Long studentId, Pageable pageable);

    List<RegistrationHistory> findBySemester_IdOrderByCreatedAtDesc(Long semesterId);

    List<RegistrationHistory> findByCourseRegistration_IdOrderByCreatedAtDesc(Long courseRegistrationId);
}
