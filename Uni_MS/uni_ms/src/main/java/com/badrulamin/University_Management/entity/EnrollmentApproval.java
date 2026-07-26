package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment_approvals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_enrollment_id", nullable = false)
    private SemesterEnrollment semesterEnrollment;

    @JsonProperty("semesterEnrollmentId")
    public void setSemesterEnrollmentId(Long id) {
        if (id != null) { this.semesterEnrollment = new SemesterEnrollment(); this.semesterEnrollment.setId(id); }
    }

    @JsonProperty
    public Long getSemesterEnrollmentId() { return this.semesterEnrollment != null ? this.semesterEnrollment.getId() : null; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advisor_id", nullable = false)
    private Teacher advisor;

    @JsonProperty("advisorId")
    public void setAdvisorId(Long id) {
        if (id != null) { this.advisor = new Teacher(); this.advisor.setId(id); }
    }

    @JsonProperty
    public Long getAdvisorId() { return this.advisor != null ? this.advisor.getId() : null; }

    @Column(nullable = false)
    private String action;

    @Column(length = 500)
    private String comments;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
