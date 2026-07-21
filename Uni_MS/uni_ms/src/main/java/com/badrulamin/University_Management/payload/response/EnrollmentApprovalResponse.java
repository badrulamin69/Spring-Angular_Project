package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnrollmentApprovalResponse {
    private Long id;
    private Long enrollmentId;
    private String enrollmentNumber;
    private Long studentId;
    private String studentName;
    private String studentCode;
    private Long semesterId;
    private String semesterName;
    private Long advisorId;
    private String advisorName;
    private String action;
    private String comments;
    private LocalDateTime createdAt;
}
