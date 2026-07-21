package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.ChoiceFillingConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChoiceFillingConfigRepository extends JpaRepository<ChoiceFillingConfig, Long> {

    Optional<ChoiceFillingConfig> findBySession_IdAndActive(Long sessionId, boolean active);

    List<ChoiceFillingConfig> findBySession_Id(Long sessionId);

    Optional<ChoiceFillingConfig> findByStatusAndActive(String status, boolean active);

    @Query("SELECT c FROM ChoiceFillingConfig c WHERE " +
           "(:search IS NULL OR LOWER(c.session.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:sessionId IS NULL OR c.session.id = :sessionId)")
    Page<ChoiceFillingConfig> findByFilters(@Param("search") String search,
                                            @Param("status") String status,
                                            @Param("sessionId") Long sessionId,
                                            Pageable pageable);

    long countByStatus(String status);

    @Query("SELECT c FROM ChoiceFillingConfig c WHERE c.status = 'ACTIVE' AND c.autoLockAfterDeadline = true AND c.choiceEndDate < :now")
    List<ChoiceFillingConfig> findExpiredConfigsForAutoLock(@Param("now") java.time.LocalDateTime now);
}
