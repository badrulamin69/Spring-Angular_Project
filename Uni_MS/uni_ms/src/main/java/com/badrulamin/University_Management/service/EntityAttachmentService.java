package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.EntityAttachment;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.repository.EntityAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EntityAttachmentService {

    private final EntityAttachmentRepository attachmentRepository;
    private static final String UPLOAD_DIR = "uploads/attachments/";

    @Transactional(readOnly = true)
    public Page<EntityAttachment> getAttachments(String entityType, Long entityId, Pageable pageable) {
        return attachmentRepository.findByEntityTypeAndEntityIdAndStatusOrderByCreatedAtDesc(
            entityType, entityId, EntityAttachment.AttachmentStatus.ACTIVE, pageable);
    }

    @Transactional(readOnly = true)
    public long countAttachments(String entityType, Long entityId) {
        return attachmentRepository.countByEntityTypeAndEntityId(entityType, entityId);
    }

    @Transactional
    public EntityAttachment uploadAttachment(String entityType, Long entityId, User user,
                                              MultipartFile file, String category) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String storedFilename = UUID.randomUUID() + ext;

        Path uploadPath = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadPath);
        Files.copy(file.getInputStream(), uploadPath.resolve(storedFilename));

        EntityAttachment attachment = EntityAttachment.builder()
            .entityType(entityType)
            .entityId(entityId)
            .originalFilename(originalFilename)
            .storedFilename(storedFilename)
            .path(UPLOAD_DIR + storedFilename)
            .contentType(file.getContentType())
            .size(file.getSize())
            .uploadedBy(user)
            .category(category)
            .verified(false)
            .status(EntityAttachment.AttachmentStatus.ACTIVE)
            .build();
        return attachmentRepository.save(attachment);
    }

    @Transactional
    public void deleteAttachment(Long attachmentId) {
        EntityAttachment attachment = attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new RuntimeException("Attachment not found"));
        attachment.setStatus(EntityAttachment.AttachmentStatus.ARCHIVED);
        attachmentRepository.save(attachment);
    }
}