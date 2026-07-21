package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoutineGenerateRequest {
    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    @NotNull(message = "Batch ID is required")
    private Long batchId;

    @NotNull(message = "Section ID is required")
    private Long sectionId;

    private String shift;
}
