package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SemesterEnrollmentRequest {
    @NotNull(message = "Student ID is required")
    private Long studentId;
    @NotNull(message = "Semester ID is required")
    private Long semesterId;
    private Long batchId;
    private Long programId;
    private Long facultyId;
    private Long departmentId;
    private Long advisorId;
    private Integer registeredCredits;
    private String enrollmentType = "NORMAL";
    private String remarks;
}
