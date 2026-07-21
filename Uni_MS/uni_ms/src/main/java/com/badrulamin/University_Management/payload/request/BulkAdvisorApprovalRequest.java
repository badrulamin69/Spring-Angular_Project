package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BulkAdvisorApprovalRequest {

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    @NotNull(message = "Student IDs are required")
    private List<Long> studentIds;

    @NotNull(message = "Action is required")
    private String action;

    private String comments;
}
