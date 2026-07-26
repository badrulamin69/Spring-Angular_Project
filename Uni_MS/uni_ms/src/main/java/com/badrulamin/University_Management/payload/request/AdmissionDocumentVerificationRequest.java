package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdmissionDocumentVerificationRequest {

    @NotNull(message = "Verified flag is required")
    private Boolean verified;

    private String remarks;
}
