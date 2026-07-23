package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer durationYears;
    private Long departmentId;
    private String departmentName;
    private Long programId;
    private String programName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
