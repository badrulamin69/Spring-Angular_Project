package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.DepartmentAllocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentAllocationRepository extends JpaRepository<DepartmentAllocation, Long> {
    Optional<DepartmentAllocation> findByAllocationNumber(String allocationNumber);
    Optional<DepartmentAllocation> findByRegistration_Id(Long registrationId);
    List<DepartmentAllocation> findByStatus(String status);
    Page<DepartmentAllocation> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);
    List<DepartmentAllocation> findByAllocatedProgram_IdOrderByMeritRankAsc(Long programId);

    List<DepartmentAllocation> findByConfig_Id(Long configId);

    List<DepartmentAllocation> findByConfig_IdOrderByMeritRankAsc(Long configId);

    Optional<DepartmentAllocation> findByConfig_IdAndRegistration_Id(Long configId, Long registrationId);

    long countByConfig_Id(Long configId);

    long countByConfig_IdAndStatus(Long configId, String status);

    long countByConfig_IdAndAllocatedProgram_Id(Long configId, Long programId);

    long countByConfig_IdAndAllocatedProgram_IdAndStatus(Long configId, Long programId, String status);

    long countByConfig_IdAndAllocatedProgram_IdAndShift(Long configId, Long programId, String shift);

    long countByConfig_IdAndAllocatedProgram_IdAndShiftAndStatus(Long configId, Long programId, String shift, String status);

    List<DepartmentAllocation> findByConfig_IdAndIsWaitingTrueOrderByWaitingRankAsc(Long configId);

    List<DepartmentAllocation> findByConfig_IdAndAllocatedProgram_IdAndIsWaitingTrueOrderByWaitingRankAsc(Long configId, Long programId);

    List<DepartmentAllocation> findByConfig_IdAndStatusIn(List<String> statuses);

    @Query("SELECT d FROM DepartmentAllocation d WHERE d.config.id = :configId " +
           "AND (:search IS NULL OR LOWER(d.registration.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(d.registration.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(d.registration.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(d.allocationNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR d.status = :status) " +
           "AND (:programId IS NULL OR d.allocatedProgram.id = :programId) " +
           "AND (:facultyId IS NULL OR d.allocatedFaculty.id = :facultyId) " +
           "AND (:isWaiting IS NULL OR d.isWaiting = :isWaiting)")
    Page<DepartmentAllocation> findByFilters(@Param("configId") Long configId,
                                              @Param("search") String search,
                                              @Param("status") String status,
                                              @Param("programId") Long programId,
                                              @Param("facultyId") Long facultyId,
                                              @Param("isWaiting") Boolean isWaiting,
                                              Pageable pageable);

    @Query("SELECT COUNT(d) FROM DepartmentAllocation d WHERE d.config.id = :configId " +
           "AND d.status = :status AND d.isWaiting = :isWaiting")
    long countByConfigAndStatusAndWaiting(@Param("configId") Long configId,
                                           @Param("status") String status,
                                           @Param("isWaiting") boolean isWaiting);

    boolean existsByConfig_IdAndRegistration_Id(Long configId, Long registrationId);

    boolean existsByConfig_IdAndRegistration_IdAndStatusIn(Long configId, Long registrationId, List<String> statuses);

    @Query("SELECT d FROM DepartmentAllocation d WHERE d.config.id = :configId " +
           "AND d.status = 'EXPIRED' AND d.allocatedProgram.id = :programId " +
           "AND d.isWaiting = false ORDER BY d.meritRank ASC")
    List<DepartmentAllocation> findExpiredAllocationsForProgram(@Param("configId") Long configId,
                                                                 @Param("programId") Long programId);
}
