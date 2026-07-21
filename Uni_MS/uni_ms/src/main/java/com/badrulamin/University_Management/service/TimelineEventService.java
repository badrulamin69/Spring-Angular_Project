package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.TimelineEvent;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.repository.TimelineEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimelineEventService {

    private final TimelineEventRepository timelineRepository;

    @Transactional(readOnly = true)
    public Page<TimelineEvent> getTimeline(String entityType, Long entityId, Pageable pageable) {
        return timelineRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            entityType, entityId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<TimelineEvent> getRecentTimeline(String entityType, Long entityId) {
        return timelineRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            entityType, entityId, Pageable.ofSize(20));
    }

    @Transactional
    public TimelineEvent recordEvent(String entityType, Long entityId, User user,
                                      TimelineEvent.EventType eventType, String description,
                                      String oldValue, String newValue, String ipAddress) {
        TimelineEvent event = TimelineEvent.builder()
            .entityType(entityType)
            .entityId(entityId)
            .user(user)
            .eventType(eventType)
            .description(description)
            .oldValue(oldValue)
            .newValue(newValue)
            .ipAddress(ipAddress)
            .severity(eventType == TimelineEvent.EventType.DELETED
                ? TimelineEvent.EventSeverity.WARNING
                : TimelineEvent.EventSeverity.INFO)
            .build();
        return timelineRepository.save(event);
    }

    @Transactional(readOnly = true)
    public long countEvents(String entityType, Long entityId) {
        return timelineRepository.countByEntityTypeAndEntityId(entityType, entityId);
    }
}
