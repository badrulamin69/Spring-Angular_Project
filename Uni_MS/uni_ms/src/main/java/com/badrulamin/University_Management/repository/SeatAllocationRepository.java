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
    List<SeatAllocation> findByTestId(Long testId);
    Optional<SeatAllocation> findByTestIdAndRegistrationId(Long testId, Long registrationId);
    Optional<SeatAllocation> findByRollNumber(String rollNumber);
    Optional<SeatAllocation> findByTestIdAndSeatNumber(Long testId, String seatNumber);
    boolean existsByTestIdAndSeatNumber(Long testId, String seatNumber);
    boolean existsByTestIdAndRollNumber(Long testId, String rollNumber);
    long countByTestId(Long testId);
    long countByTestIdAndStatus(Long testId, String status);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(sa.rollNumber, LENGTH(sa.rollNumber) - 4) AS long)), 0) FROM SeatAllocation sa WHERE sa.test.id = :testId")
    Long findMaxRollSequence(@Param("testId") Long testId);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(sa.seatNumber, LENGTH(sa.seatNumber) - 3) AS long)), 0) FROM SeatAllocation sa WHERE sa.test.id = :testId AND sa.roomName = :roomName")
    Long findMaxSeatSequenceInRoom(@Param("testId") Long testId, @Param("roomName") String roomName);
}
