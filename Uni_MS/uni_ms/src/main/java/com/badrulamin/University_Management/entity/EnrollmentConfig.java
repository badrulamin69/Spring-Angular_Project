package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_session_id")
    private AcademicSession academicSession;

    @JsonProperty("academicSessionId")
    public void setAcademicSessionId(Long id) {
        if (id != null) { this.academicSession = new AcademicSession(); this.academicSession.setId(id); }
    }

    @JsonProperty
    public Long getAcademicSessionId() { return this.academicSession != null ? this.academicSession.getId() : null; }

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

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "late_enrollment_date")
    private LocalDate lateEnrollmentDate;

    @NotNull
    @Column(name = "min_credits", nullable = false)
    private Integer minCredits = 12;

    @NotNull
    @Column(name = "max_credits", nullable = false)
    private Integer maxCredits = 24;

    @Column(name = "enrollment_status", nullable = false)
    private String enrollmentStatus = "OPEN";

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isClosed = false;

    @Column(name = "requires_advisor_approval", nullable = false)
    private Boolean requiresAdvisorApproval = true;

    @Column(name = "requires_payment", nullable = false)
    private Boolean requiresPayment = true;

    @Column(name = "allow_late_enrollment", nullable = false)
    private Boolean allowLateEnrollment = true;

    @Column(length = 500)
    private String remarks;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public boolean isEnrollmentOpen(LocalDate date) {
        return isActive && !isClosed && "OPEN".equals(enrollmentStatus)
                && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public boolean isLateEnrollmentAllowed(LocalDate date) {
        return isActive && !isClosed && allowLateEnrollment
                && lateEnrollmentDate != null
                && !date.isBefore(startDate) && !date.isAfter(lateEnrollmentDate);
    }
}
