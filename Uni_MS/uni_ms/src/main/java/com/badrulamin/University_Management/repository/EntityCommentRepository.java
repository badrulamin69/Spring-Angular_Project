package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.EntityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityCommentRepository extends JpaRepository<EntityComment, Long> {

    Page<EntityComment> findByEntityTypeAndEntityIdAndStatusOrderByCreatedAtDesc(
        String entityType, Long entityId, EntityComment.CommentStatus status, Pageable pageable);

    List<EntityComment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    long countByEntityTypeAndEntityId(String entityType, Long entityId);

    List<EntityComment> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
