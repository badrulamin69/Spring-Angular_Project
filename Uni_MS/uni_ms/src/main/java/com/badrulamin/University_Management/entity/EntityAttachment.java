package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "entity_attachments", indexes = {
    @Index(name = "idx_attachment_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_attachment_uploader", columnList = "uploaded_by_id")
})
public class EntityAttachment extends BaseEntity {

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;

    @Column(nullable = false)
    private String path;

    @Column(name = "content_type", length = 255)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private User uploadedBy;

    @Column(length = 50)
    private String category;

    @Column(name = "is_verified", nullable = false)
    private Boolean verified = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AttachmentStatus status = AttachmentStatus.ACTIVE;

    public enum AttachmentStatus {
        ACTIVE, ARCHIVED, DELETED
    }

    public String getFormattedSize() {
        if (size < 1024) return size + " B";
        if (size < 1048576) return String.format("%.1f KB", size / 1024.0);
        if (size < 1073741824) return String.format("%.1f MB", size / 1048576.0);
        return String.format("%.2f GB", size / 1073741824.0);
    }
}
