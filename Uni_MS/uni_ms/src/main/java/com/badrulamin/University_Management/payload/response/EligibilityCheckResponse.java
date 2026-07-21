package com.badrulamin.University_Management.payload.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EligibilityCheckResponse {
    private Long studentId;
    private String studentName;
    private Long semesterId;
    private String semesterName;
    private boolean eligible;
    private Integer totalCreditsRegistered;
    private Integer minCreditsRequired;
    private Integer maxCreditsAllowed;
    private String status;
    private java.util.List<String> errors;
    private java.util.List<String> warnings;
    private LocalDateTime checkedAt;
}
