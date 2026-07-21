package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AdvisorApprovalRequest {

    @NotNull(message = "Registration IDs are required")
    private List<Long> registrationIds;

    @NotNull(message = "Action is required")
    private String action;

    private String comments;
}
