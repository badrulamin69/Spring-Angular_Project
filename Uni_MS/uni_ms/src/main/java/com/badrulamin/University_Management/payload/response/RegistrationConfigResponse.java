package com.badrulamin.University_Management.payload.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RegistrationConfigResponse {
    private Long id;
    private Long semesterId;
    private String semesterName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer minCredits;
    private Integer maxCredits;
    private Boolean allowAddDrop;
    private LocalDate addDropDeadline;
    private Boolean advisorApprovalRequired;
    private Boolean paymentRequired;
    private Boolean isActive;
    private Boolean isClosed;
    private String status;
    private String remarks;
    private LocalDateTime createdAt;
}
