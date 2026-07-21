package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.EntityComment;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.repository.EntityCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EntityCommentService {

    private final EntityCommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Page<EntityComment> getComments(String entityType, Long entityId, Pageable pageable) {
        return commentRepository.findByEntityTypeAndEntityIdAndStatusOrderByCreatedAtDesc(
            entityType, entityId, EntityComment.CommentStatus.ACTIVE, pageable);
    }

    @Transactional(readOnly = true)
    public long countComments(String entityType, Long entityId) {
        return commentRepository.countByEntityTypeAndEntityId(entityType, entityId);
    }

    @Transactional
    public EntityComment addComment(String entityType, Long entityId, User user, String content, Long parentId) {
        EntityComment comment = EntityComment.builder()
            .entityType(entityType)
            .entityId(entityId)
            .user(user)
            .content(content)
            .parentId(parentId)
            .edited(false)
            .status(EntityComment.CommentStatus.ACTIVE)
            .build();
        return commentRepository.save(comment);
    }

    @Transactional
    public EntityComment updateComment(Long commentId, String content, Long currentUserId) {
        EntityComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("You can only edit your own comments");
        }
        comment.setContent(content);
        comment.setEdited(true);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {
        EntityComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("You can only delete your own comments");
        }
        comment.setStatus(EntityComment.CommentStatus.DELETED);
        commentRepository.save(comment);
    }
}
