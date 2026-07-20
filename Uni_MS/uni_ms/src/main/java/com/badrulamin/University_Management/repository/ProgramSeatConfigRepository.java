package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.ProgramSeatConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramSeatConfigRepository extends JpaRepository<ProgramSeatConfig, Long> {

    List<ProgramSeatConfig> findByConfig_Id(Long configId);

    List<ProgramSeatConfig> findByConfig_IdAndIsActive(Long configId, boolean isActive);

    Optional<ProgramSeatConfig> findByConfig_IdAndProgram_Id(Long configId, Long programId);

    Optional<ProgramSeatConfig> findByConfig_IdAndProgram_IdAndShift(Long configId, Long programId, String shift);

    @Query("SELECT p FROM ProgramSeatConfig p WHERE p.config.id = :configId " +
           "AND p.program.id = :programId AND p.shift = :shift AND p.isActive = true")
    Optional<ProgramSeatConfig> findActiveSeatConfig(@Param("configId") Long configId,
                                                      @Param("programId") Long programId,
                                                      @Param("shift") String shift);

    @Query("SELECT p FROM ProgramSeatConfig p WHERE p.config.id = :configId AND p.isActive = true " +
           "AND (p.totalSeats - p.allocatedSeats) > 0")
    List<ProgramSeatConfig> findProgramsWithAvailableSeats(@Param("configId") Long configId);

    @Query("SELECT COALESCE(SUM(p.totalSeats), 0) FROM ProgramSeatConfig p WHERE p.config.id = :configId AND p.isActive = true")
    long sumTotalSeatsByConfig(@Param("configId") Long configId);

    @Query("SELECT COALESCE(SUM(p.allocatedSeats), 0) FROM ProgramSeatConfig p WHERE p.config.id = :configId AND p.isActive = true")
    long sumAllocatedSeatsByConfig(@Param("configId") Long configId);

    @Query("SELECT p FROM ProgramSeatConfig p WHERE p.config.id = :configId AND p.faculty.id = :facultyId AND p.isActive = true")
    List<ProgramSeatConfig> findByConfigAndFaculty(@Param("configId") Long configId, @Param("facultyId") Long facultyId);

    @Query("SELECT p FROM ProgramSeatConfig p WHERE p.config.id = :configId AND p.department.id = :departmentId AND p.isActive = true")
    List<ProgramSeatConfig> findByConfigAndDepartment(@Param("configId") Long configId, @Param("departmentId") Long departmentId);
}
