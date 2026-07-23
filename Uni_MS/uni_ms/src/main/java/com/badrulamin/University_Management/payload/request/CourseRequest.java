package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequest {
    @NotBlank(message = "Course name is required")
    private String name;

    @NotBlank(message = "Course code is required")
    private String code;

    private String description;

    @NotNull(message = "Duration is required")
    private Integer durationYears;

    @NotNull(message = "Department is required")
    private Long departmentId;

    private Long programId;
}
