package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExamResponse {
    private Long id;
    private String name;
    private String examType;
    private Long courseId;
    private String courseName;
    private Long subjectId;
    private String subjectName;
    private Integer totalMarks;
    private Integer passingMarks;
    private LocalDate examDate;
    private Integer durationMinutes;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
