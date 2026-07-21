package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "entity_comments", indexes = {
    @Index(name = "idx_comment_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_comment_user", columnList = "user_id")
})
public class EntityComment extends BaseEntity {

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "is_edited", nullable = false)
    private Boolean edited = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CommentStatus status = CommentStatus.ACTIVE;

    public enum CommentStatus {
        ACTIVE, EDITED, DELETED
    }
}
