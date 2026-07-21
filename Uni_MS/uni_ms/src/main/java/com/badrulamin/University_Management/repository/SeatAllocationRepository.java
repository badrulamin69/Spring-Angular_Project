package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.SeatAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Long> {
    List<SeatAllocation> findByTest_Id(Long testId);
    Optional<SeatAllocation> findByTest_IdAndRegistration_Id(Long testId, Long registrationId);
    Optional<SeatAllocation> findByRollNumber(String rollNumber);
    Optional<SeatAllocation> findByTest_IdAndSeatNumber(Long testId, String seatNumber);
    boolean existsByTest_IdAndSeatNumber(Long testId, String seatNumber);
    boolean existsByTest_IdAndRollNumber(Long testId, String rollNumber);
    long countByTest_Id(Long testId);
    long countByTest_IdAndStatus(Long testId, String status);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(sa.rollNumber, LENGTH(sa.rollNumber) - 4) AS long)), 0) FROM SeatAllocation sa WHERE sa.test.id = :testId")
    Long findMaxRollSequence(@Param("testId") Long testId);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(sa.seatNumber, LENGTH(sa.seatNumber) - 3) AS long)), 0) FROM SeatAllocation sa WHERE sa.test.id = :testId AND sa.roomName = :roomName")
    Long findMaxSeatSequenceInRoom(@Param("testId") Long testId, @Param("roomName") String roomName);
}
