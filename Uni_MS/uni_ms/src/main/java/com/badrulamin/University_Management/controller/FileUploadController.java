package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.service.FileUploadService;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    @PostMapping("/{module}")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @PathVariable String module,
            @RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadFile(file, module);
            return ResponseEntity.ok(ApiResponse.success(Map.of("url", url, "filename", file.getOriginalFilename())));
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
}
