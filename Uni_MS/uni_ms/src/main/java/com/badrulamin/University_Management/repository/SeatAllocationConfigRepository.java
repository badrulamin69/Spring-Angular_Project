package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.SeatAllocationConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatAllocationConfigRepository extends JpaRepository<SeatAllocationConfig, Long> {

    Optional<SeatAllocationConfig> findBySession_IdAndStatus(Long sessionId, String status);

    List<SeatAllocationConfig> findBySession_Id(Long sessionId);

    @Query("SELECT c FROM SeatAllocationConfig c WHERE " +
           "(:search IS NULL OR LOWER(c.session.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.academicYear) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:sessionId IS NULL OR c.session.id = :sessionId)")
    Page<SeatAllocationConfig> findByFilters(@Param("search") String search,
                                              @Param("status") String status,
                                              @Param("sessionId") Long sessionId,
                                              Pageable pageable);

    long countByStatus(String status);
}
