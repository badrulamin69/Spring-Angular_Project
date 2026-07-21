package com.badrulamin.University_Management.payload.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RegistrationSummaryResponse {
    private Long studentId;
    private String studentName;
    private String studentCode;
    private Long semesterId;
    private String semesterName;
    private Long batchId;
    private String batchName;
    private Integer totalCreditsRegistered;
    private Integer minCreditsRequired;
    private Integer maxCreditsAllowed;
    private String registrationStatus;
    private String advisorApprovalStatus;
    private String paymentStatus;
    private Boolean isFinalized;
    private List<RegisteredCourseItem> registeredCourses;
    private List<String> errors;
    private LocalDateTime lastUpdated;

    @Data
    public static class RegisteredCourseItem {
        private Long registrationId;
        private Long subjectId;
        private String subjectName;
        private String subjectCode;
        private Integer creditHours;
        private String status;
        private String advisorStatus;
        private String paymentStatus;
    }
}
