package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<ActivityLog> findByModuleOrderByCreatedAtDesc(String module, Pageable pageable);

    Page<ActivityLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);

    List<ActivityLog> findTop20ByOrderByCreatedAtDesc();

    Long countByModule(String module);

    Long countByUser_Id(Long userId);

    Page<ActivityLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
