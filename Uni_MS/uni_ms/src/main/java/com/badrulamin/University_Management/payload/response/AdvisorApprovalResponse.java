package com.badrulamin.University_Management.payload.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdvisorApprovalResponse {
    private Long studentId;
    private String studentName;
    private String studentCode;
    private Long semesterId;
    private String semesterName;
    private Integer totalCredits;
    private String approvalAction;
    private String comments;
    private List<Long> processedRegistrationIds;
    private LocalDateTime processedAt;
}
