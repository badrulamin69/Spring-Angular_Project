package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.EntityComment;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.service.EntityCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class EntityCommentController {

    private final EntityCommentService commentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EntityComment>>> getComments(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            commentService.getComments(entityType, entityId, PageRequest.of(page, size))));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countComments(
            @RequestParam String entityType,
            @RequestParam Long entityId) {
        return ResponseEntity.ok(ApiResponse.success(
            Map.of("count", commentService.countComments(entityType, entityId))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EntityComment>> addComment(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal User user) {
        String entityType = (String) body.get("entityType");
        Long entityId = ((Number) body.get("entityId")).longValue();
        String content = (String) body.get("content");
        Long parentId = body.get("parentId") != null ? ((Number) body.get("parentId")).longValue() : null;
        return ResponseEntity.ok(ApiResponse.success(commentService.addComment(entityType, entityId, user, content, parentId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EntityComment>> updateComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(commentService.updateComment(id, body.get("content"), user.getId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        commentService.deleteComment(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
