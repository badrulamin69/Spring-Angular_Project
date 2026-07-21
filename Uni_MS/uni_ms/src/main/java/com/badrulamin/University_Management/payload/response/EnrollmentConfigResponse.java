package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EnrollmentConfigResponse {
    private Long id;
    private Long semesterId;
    private String semesterName;
    private Long academicSessionId;
    private String academicSessionName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lateEnrollmentDate;
    private Integer minCredits;
    private Integer maxCredits;
    private String enrollmentStatus;
    private Boolean isActive;
    private Boolean isClosed;
    private Boolean requiresAdvisorApproval;
    private Boolean requiresPayment;
    private Boolean allowLateEnrollment;
    private String remarks;
    private LocalDateTime createdAt;
}
