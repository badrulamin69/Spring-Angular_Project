package com.badrulamin.University_Management.payload.request;

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
    private Integer totalMarks;

    @NotNull(message = "Passing marks is required")
    private Integer passingMarks;

    private LocalDate examDate;

    private Integer durationMinutes;

    private String description;
}
