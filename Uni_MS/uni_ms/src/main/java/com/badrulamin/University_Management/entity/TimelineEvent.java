package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "timeline_events", indexes = {
    @Index(name = "idx_timeline_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_timeline_user", columnList = "user_id"),
    @Index(name = "idx_timeline_created", columnList = "created_at")
})
public class TimelineEvent extends BaseEntity {

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventType eventType;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private EventSeverity severity = EventSeverity.INFO;

    public enum EventType {
        CREATED, UPDATED, DELETED, STATUS_CHANGED,
        APPROVED, REJECTED, SUBMITTED, CANCELLED,
        COMMENT_ADDED, ATTACHMENT_UPLOADED, ATTACHMENT_REMOVED,
        ASSIGNMENT_CHANGED, PRIORITY_CHANGED, DUE_DATE_CHANGED
    }

    public enum EventSeverity {
        INFO, WARNING, ERROR, SUCCESS
    }
}
