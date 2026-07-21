package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminEnrollmentActionRequest {
    @NotNull(message = "Enrollment ID is required")
    private Long enrollmentId;
    private String reason;
}
