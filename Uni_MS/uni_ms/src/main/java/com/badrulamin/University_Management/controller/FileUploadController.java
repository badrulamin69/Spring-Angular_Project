package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.service.FileUploadService;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx",
            "xls", "xlsx", "ppt", "pptx", "txt", "csv"
    );

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @PostMapping("/{module}")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @PathVariable String module,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File is empty"));
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File size exceeds maximum allowed size of 10MB"));
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Filename is required"));
        }
        String extension = getExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    "File type '" + extension + "' is not allowed. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS)));
        }
        try {
            String url = fileUploadService.uploadFile(file, module);
            return ResponseEntity.ok(ApiResponse.success(Map.of("url", url, "filename", originalFilename)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteFile(@RequestParam String url) {
        try {
            fileUploadService.deleteFile(url);
            return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1);
    }
}
