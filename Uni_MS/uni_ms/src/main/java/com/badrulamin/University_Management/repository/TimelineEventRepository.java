package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.TimelineEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimelineEventRepository extends JpaRepository<TimelineEvent, Long> {

    Page<TimelineEvent> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
        String entityType, Long entityId, Pageable pageable);

    List<TimelineEvent> findTop20ByEntityTypeAndEntityIdOrderByCreatedAtDesc(
        String entityType, Long entityId);

    long countByEntityTypeAndEntityId(String entityType, Long entityId);

    List<TimelineEvent> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByEventType(TimelineEvent.EventType eventType);
}
