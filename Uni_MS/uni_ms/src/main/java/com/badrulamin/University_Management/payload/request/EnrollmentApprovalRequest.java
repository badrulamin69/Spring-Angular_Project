package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentApprovalRequest {
    @NotNull(message = "Enrollment ID is required")
    private Long enrollmentId;
    @NotNull(message = "Action is required")
    private String action;
    private String comments;
}
