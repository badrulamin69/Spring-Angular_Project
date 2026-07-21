package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.util.List;

@Data
public class EnrollmentEligibilityResponse {
    private Long studentId;
    private String studentName;
    private Long semesterId;
    private String semesterName;
    private boolean eligible;
    private List<String> errors;
    private List<String> warnings;
    private boolean hasActiveEnrollment;
    private boolean hasAcademicHold;
    private boolean hasFinancialHold;
    private boolean registrationCompleted;
    private boolean feesPaid;
    private Integer currentOutstandingBalance;
}
