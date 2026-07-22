package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.EntityAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityAttachmentRepository extends JpaRepository<EntityAttachment, Long> {

    Page<EntityAttachment> findByEntityTypeAndEntityIdAndStatusOrderByCreatedAtDesc(
        String entityType, Long entityId, EntityAttachment.AttachmentStatus status, Pageable pageable);

    List<EntityAttachment> findByEntityTypeAndEntityIdAndCategory(
        String entityType, Long entityId, String category);

    long countByEntityTypeAndEntityId(String entityType, Long entityId);

    @Query("SELECT COALESCE(SUM(a.size), 0) FROM EntityAttachment a WHERE a.entityType = :entityType AND a.entityId = :entityId AND a.status = 'ACTIVE'")
    long sumSizeByEntityTypeAndEntityId(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    List<EntityAttachment> findByUploadedBy_IdOrderByCreatedAtDesc(Long userId);
}
