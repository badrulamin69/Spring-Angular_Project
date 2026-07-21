package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BulkRegistrationRequest {

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    @NotNull(message = "Batch ID is required")
    private Long batchId;

    @NotNull(message = "Student IDs are required")
    private java.util.List<Long> studentIds;
}
