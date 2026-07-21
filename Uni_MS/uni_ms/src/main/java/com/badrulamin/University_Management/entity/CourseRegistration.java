package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRegistration extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @Column(nullable = false)
    private String status;

    private LocalDate registrationDate;

    private Boolean isSelected;

    private Integer creditHours;

    @Column(length = 500)
    private String remarks;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "registration_type", length = 20)
    private String registrationType;

    @Column(name = "advisor_status", length = 20)
    private String advisorStatus;

    @Column(name = "advisor_comments", length = 500)
    private String advisorComments;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "advisor_id")
    private Faculty advisor;

    @JsonProperty("advisorId")
    public Long getAdvisorId() { return advisor != null ? advisor.getId() : null; }
    @JsonProperty("advisorId")
    public void setAdvisorId(Long v) { if (v != null) { Faculty f = new Faculty(); f.setId(v); this.advisor = f; } }

    @Column(name = "advisor_approved_at")
    private LocalDateTime advisorApprovedAt;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "payment_amount")
    private Double paymentAmount;

    @Column(name = "finalized", nullable = false)
    private Boolean finalized = false;

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    @JsonProperty("studentId")
    public Long getStudentId() { return student != null ? student.getId() : null; }
    @JsonProperty("studentId")
    public void setStudentId(Long v) { if (v != null) { Student s = new Student(); s.setId(v); this.student = s; } }

    @JsonProperty("courseId")
    public Long getCourseId() { return course != null ? course.getId() : null; }
    @JsonProperty("courseId")
    public void setCourseId(Long v) { if (v != null) { Course c = new Course(); c.setId(v); this.course = c; } }

    @JsonProperty("semesterId")
    public Long getSemesterId() { return semester != null ? semester.getId() : null; }
    @JsonProperty("semesterId")
    public void setSemesterId(Long v) { if (v != null) { Semester s = new Semester(); s.setId(v); this.semester = s; } }

    @JsonProperty("batchId")
    public Long getBatchId() { return batch != null ? batch.getId() : null; }
    @JsonProperty("batchId")
    public void setBatchId(Long v) { if (v != null) { Batch b = new Batch(); b.setId(v); this.batch = b; } }

    @JsonProperty("approvedById")
    public Long getApprovedById() { return approvedBy != null ? approvedBy.getId() : null; }
    @JsonProperty("approvedById")
    public void setApprovedById(Long v) { if (v != null) { User u = new User(); u.setId(v); this.approvedBy = u; } }
}
