package com.badrulamin.University_Management.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${image.upload.dir:uploads/}")
    private String uploadDir;

    public String uploadFile(MultipartFile file, String subfolder) throws IOException {
        Path uploadPath = Paths.get(uploadDir, subfolder).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        Path targetLocation = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + subfolder + "/" + filename;
    }

    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) return;
        String relativePath = fileUrl.replaceFirst("^/uploads/", "");
        Path filePath = Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();
        Files.deleteIfExists(filePath);
    }

    public byte[] getFile(String fileUrl) throws IOException {
        String relativePath = fileUrl.replaceFirst("^/uploads/", "");
        Path filePath = Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();
        return Files.readAllBytes(filePath);
    }
}
