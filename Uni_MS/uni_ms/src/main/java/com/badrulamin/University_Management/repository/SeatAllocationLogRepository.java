package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.SeatAllocationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatAllocationLogRepository extends JpaRepository<SeatAllocationLog, Long> {

    List<SeatAllocationLog> findByAllocation_IdOrderByPerformedAtDesc(Long allocationId);

    Page<SeatAllocationLog> findByAllocation_Config_Id(Long configId, Pageable pageable);

    @Query("SELECT l FROM SeatAllocationLog l WHERE l.allocation.config.id = :configId " +
           "AND (:action IS NULL OR l.action = :action) " +
           "ORDER BY l.performedAt DESC")
    Page<SeatAllocationLog> findByConfigAndAction(@Param("configId") Long configId,
                                                   @Param("action") String action,
                                                   Pageable pageable);

    long countByAllocation_Config_IdAndAction(Long configId, String action);
}
