package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRegistrationRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    private Long batchId;

    private String registrationType;

    private String remarks;
}
