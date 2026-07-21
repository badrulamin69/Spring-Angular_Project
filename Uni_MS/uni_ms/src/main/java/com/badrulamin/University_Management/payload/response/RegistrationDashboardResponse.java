package com.badrulamin.University_Management.payload.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RegistrationDashboardResponse {
    private Long totalRegistrations;
    private Long pendingApprovals;
    private Long approvedRegistrations;
    private Long registeredStudents;
    private Long droppedRegistrations;
    private List<RegistrationStatsByStatus> statusBreakdown;
    private List<RecentRegistration> recentRegistrations;

    @Data
    public static class RegistrationStatsByStatus {
        private String status;
        private Long count;
    }

    @Data
    public static class RecentRegistration {
        private Long id;
        private String studentName;
        private String studentCode;
        private String courseName;
        private String semesterName;
        private String status;
        private Integer creditHours;
        private LocalDateTime registrationDate;
    }
}
