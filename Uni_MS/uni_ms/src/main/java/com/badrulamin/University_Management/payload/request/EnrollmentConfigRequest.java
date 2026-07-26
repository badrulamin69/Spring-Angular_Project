package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EnrollmentConfigRequest {
    @NotNull(message = "Semester ID is required")
    private Long semesterId;
    private Long academicSessionId;
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    private LocalDate lateEnrollmentDate;
    @NotNull(message = "Min credits is required")
    @Min(value = 1, message = "Min credits must be at least 1")
    private Integer minCredits = 12;
    @NotNull(message = "Max credits is required")
    @Min(value = 1, message = "Max credits must be at least 1")
    private Integer maxCredits = 24;
    private String enrollmentStatus = "OPEN";
    private Boolean isActive = true;
    private Boolean requiresAdvisorApproval = true;
    private Boolean requiresPayment = true;
    private Boolean allowLateEnrollment = true;
    private String remarks;
}
