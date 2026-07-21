package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @JsonProperty("studentId")
    public void setStudentId(Long id) {
        if (id != null) { this.student = new Student(); this.student.setId(id); }
    }

    @JsonProperty
    public Long getStudentId() { return this.student != null ? this.student.getId() : null; }

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
    @JoinColumn(name = "semester_enrollment_id")
    private SemesterEnrollment semesterEnrollment;

    @JsonProperty("semesterEnrollmentId")
    public void setSemesterEnrollmentId(Long id) {
        if (id != null) { this.semesterEnrollment = new SemesterEnrollment(); this.semesterEnrollment.setId(id); }
    }

    @JsonProperty
    public Long getSemesterEnrollmentId() { return this.semesterEnrollment != null ? this.semesterEnrollment.getId() : null; }

    @Column(nullable = false)
    private String action;

    @Column(nullable = false, length = 1000)
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @JsonProperty("performedById")
    public void setPerformedById(Long id) {
        if (id != null) { this.performedBy = new User(); this.performedBy.setId(id); }
    }

    @JsonProperty
    public Long getPerformedById() { return this.performedBy != null ? this.performedBy.getId() : null; }

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
