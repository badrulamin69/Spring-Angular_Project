package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "semester_enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterEnrollment extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @JsonProperty("studentId")
    public void setStudentId(Long id) {
        if (id != null) { this.student = new Student(); this.student.setId(id); }
    }

    @JsonProperty
    public Long getStudentId() { return this.student != null ? this.student.getId() : null; }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @JsonProperty("semesterId")
    public void setSemesterId(Long id) {
        if (id != null) { this.semester = new Semester(); this.semester.setId(id); }
    }

    @JsonProperty
    public Long getSemesterId() { return this.semester != null ? this.semester.getId() : null; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @JsonProperty("batchId")
    public void setBatchId(Long id) {
        if (id != null) { this.batch = new Batch(); this.batch.setId(id); }
    }

    @JsonProperty
    public Long getBatchId() { return this.batch != null ? this.batch.getId() : null; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_session_id")
    private AcademicSession academicSession;

    @JsonProperty("academicSessionId")
    public void setAcademicSessionId(Long id) {
        if (id != null) { this.academicSession = new AcademicSession(); this.academicSession.setId(id); }
    }

    @JsonProperty
    public Long getAcademicSessionId() { return this.academicSession != null ? this.academicSession.getId() : null; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @JsonProperty("programId")
    public void setProgramId(Long id) {
        if (id != null) { this.program = new Program(); this.program.setId(id); }
    }

    @JsonProperty
    public Long getProgramId() { return this.program != null ? this.program.getId() : null; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @JsonProperty("facultyId")
    public void setFacultyId(Long id) {
        if (id != null) { this.faculty = new Faculty(); this.faculty.setId(id); }
    }

    @JsonProperty
    public Long getFacultyId() { return this.faculty != null ? this.faculty.getId() : null; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @JsonProperty("departmentId")
    public void setDepartmentId(Long id) {
        if (id != null) { this.department = new Department(); this.department.setId(id); }
    }

    @JsonProperty
    public Long getDepartmentId() { return this.department != null ? this.department.getId() : null; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advisor_id")
    private Teacher advisor;

    @JsonProperty("advisorId")
    public void setAdvisorId(Long id) {
        if (id != null) { this.advisor = new Teacher(); this.advisor.setId(id); }
    }

    @JsonProperty
    public Long getAdvisorId() { return this.advisor != null ? this.advisor.getId() : null; }

    @Column(name = "enrollment_number", unique = true, length = 50)
    private String enrollmentNumber;

    @NotNull
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(nullable = false)
    @Builder.Default
    private String status = "DRAFT";

    @Column(name = "registered_credits")
    @Builder.Default
    private Integer registeredCredits = 0;

    @Column(name = "min_credits")
    private Integer minCredits;

    @Column(name = "max_credits")
    private Integer maxCredits;

    @Column(name = "advisor_status", length = 20)
    private String advisorStatus;

    @Column(name = "advisor_comments", length = 500)
    private String advisorComments;

    @Column(name = "advisor_approved_at")
    private LocalDateTime advisorApprovedAt;

    @Column(name = "payment_status", length = 20)
    @Builder.Default
    private String paymentStatus = "PENDING";

    @Column(name = "payment_amount")
    private Double paymentAmount;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "is_finalized", nullable = false)
    @Builder.Default
    private Boolean isFinalized = false;

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    @Column(length = 500)
    private String remarks;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "enrollment_type", length = 20)
    @Builder.Default
    private String enrollmentType = "NORMAL";

    @Column(name = "is_late_enrollment", nullable = false)
    @Builder.Default
    private Boolean isLateEnrollment = false;

    public boolean canBeCancelled() {
        return "APPROVED".equals(this.status) || "PENDING".equals(this.status) || "DRAFT".equals(this.status);
    }

    public boolean isEnrollmentOpen() {
        return "DRAFT".equals(this.status) || "PENDING".equals(this.status);
    }
}
