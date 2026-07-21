package com.badrulamin.University_Management.payload.response;

import lombok.Data;

@Data
public class EnrollmentDashboardResponse {
    private Long totalEnrollments;
    private Long pendingApprovals;
    private Long approvedEnrollments;
    private Long completedEnrollments;
    private Long rejectedEnrollments;
    private Long cancelledEnrollments;
    private Long draftEnrollments;
    private java.util.List<EnrollmentStatsByStatus> statusBreakdown;
    private java.util.List<EnrollmentStatsByDepartment> departmentBreakdown;
    private java.util.List<RecentEnrollment> recentEnrollments;

    @Data
    public static class EnrollmentStatsByStatus {
        private String status;
        private Long count;
    }

    @Data
    public static class EnrollmentStatsByDepartment {
        private Long departmentId;
        private String departmentName;
        private Long count;
    }

    @Data
    public static class RecentEnrollment {
        private Long id;
        private String enrollmentNumber;
        private String studentName;
        private String studentCode;
        private String semesterName;
        private String status;
        private Integer registeredCredits;
        private String advisorStatus;
        private String paymentStatus;
        private String createdAt;
    }
}
