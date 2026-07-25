package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ExamRequest {
    @NotBlank(message = "Exam name is required")
    private String name;

    @NotBlank(message = "Exam type is required")
    private String examType;

    @NotNull(message = "Course is required")
    private Long courseId;

    @NotNull(message = "Subject is required")
    private Long subjectId;

    @NotNull(message = "Total marks is required")
    @Min(value = 1, message = "Total marks must be at least 1")
    private Integer totalMarks;

    @NotNull(message = "Passing marks is required")
    @Min(value = 1, message = "Passing marks must be at least 1")
    private Integer passingMarks;

    private LocalDate examDate;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    private String description;
}
