package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionAttendanceRepository extends JpaRepository<AdmissionAttendance, Long> {
    List<AdmissionAttendance> findByTestId(Long testId);
    Optional<AdmissionAttendance> findByTestIdAndRegistrationId(Long testId, Long registrationId);
    long countByTestId(Long testId);
    long countByTestIdAndStatus(Long testId, String status);

    @Query("SELECT COUNT(aa) FROM AdmissionAttendance aa WHERE aa.test.id = :testId AND aa.status = 'PRESENT'")
    long countPresentByTestId(@Param("testId") Long testId);

    @Query("SELECT COUNT(aa) FROM AdmissionAttendance aa WHERE aa.test.id = :testId AND aa.status = 'ABSENT'")
    long countAbsentByTestId(@Param("testId") Long testId);

    @Query("SELECT COUNT(aa) FROM AdmissionAttendance aa WHERE aa.test.id = :testId AND aa.status = 'LATE'")
    long countLateByTestId(@Param("testId") Long testId);
}
