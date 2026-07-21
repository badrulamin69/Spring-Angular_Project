package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SemesterEnrollmentResponse {
    private Long id;
    private String enrollmentNumber;
    private Long studentId;
    private String studentName;
    private String studentCode;
    private String studentEmail;
    private Long semesterId;
    private String semesterName;
    private Long batchId;
    private String batchName;
    private Long programId;
    private String programName;
    private Long facultyId;
    private String facultyName;
    private Long departmentId;
    private String departmentName;
    private Long advisorId;
    private String advisorName;
    private LocalDate enrollmentDate;
    private String status;
    private Integer registeredCredits;
    private Integer minCredits;
    private Integer maxCredits;
    private String advisorStatus;
    private String advisorComments;
    private LocalDateTime advisorApprovedAt;
    private String paymentStatus;
    private Double paymentAmount;
    private String paymentReference;
    private LocalDateTime paymentDate;
    private Boolean isFinalized;
    private LocalDateTime finalizedAt;
    private String remarks;
    private Boolean isActive;
    private Boolean isLateEnrollment;
    private String enrollmentType;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
