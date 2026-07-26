package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdmissionDocumentSubmitRequest {

    @NotBlank(message = "Document type is required")
    @Size(max = 100, message = "Document type must not exceed 100 characters")
    private String documentType;

    @NotBlank(message = "Document name is required")
    @Size(max = 200, message = "Document name must not exceed 200 characters")
    private String documentName;

    @Size(max = 500, message = "File URL must not exceed 500 characters")
    private String fileUrl;

    private Long fileSize;
}
