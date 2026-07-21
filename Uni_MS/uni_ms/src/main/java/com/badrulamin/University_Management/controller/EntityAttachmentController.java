package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.EntityAttachment;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.service.EntityAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
public class EntityAttachmentController {

    private final EntityAttachmentService attachmentService;

    @GetMapping
    public ResponseEntity<Page<EntityAttachment>> getAttachments(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
            attachmentService.getAttachments(entityType, entityId, PageRequest.of(page, size)));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countAttachments(
            @RequestParam String entityType,
            @RequestParam Long entityId) {
        return ResponseEntity.ok(
            Map.of("count", attachmentService.countAttachments(entityType, entityId)));
    }

    @PostMapping("/upload")
    public ResponseEntity<EntityAttachment> uploadAttachment(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestParam(required = false) String category,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) throws IOException {
        return ResponseEntity.ok(
            attachmentService.uploadAttachment(entityType, entityId, user, file, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }
}
