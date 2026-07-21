package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @JsonProperty("studentId")
    public void setStudentId(Long id) {
        if (id != null) {
            this.student = new Student();
            this.student.setId(id);
        }
    }

    @JsonProperty
    public Long getStudentId() {
        return this.student != null ? this.student.getId() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @JsonProperty("courseId")
    public void setCourseId(Long id) {
        if (id != null) {
            this.course = new Course();
            this.course.setId(id);
        }
    }

    @JsonProperty
    public Long getCourseId() {
        return this.course != null ? this.course.getId() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @JsonProperty("semesterId")
    public void setSemesterId(Long id) {
        if (id != null) {
            this.semester = new Semester();
            this.semester.setId(id);
        }
    }

    @JsonProperty
    public Long getSemesterId() {
        return this.semester != null ? this.semester.getId() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_registration_id")
    private CourseRegistration courseRegistration;

    @JsonProperty("courseRegistrationId")
    public void setCourseRegistrationId(Long id) {
        if (id != null) {
            this.courseRegistration = new CourseRegistration();
            this.courseRegistration.setId(id);
        }
    }

    @JsonProperty
    public Long getCourseRegistrationId() {
        return this.courseRegistration != null ? this.courseRegistration.getId() : null;
    }

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @JsonProperty("performedById")
    public void setPerformedById(Long id) {
        if (id != null) {
            this.performedBy = new User();
            this.performedBy.setId(id);
        }
    }

    @JsonProperty
    public Long getPerformedById() {
        return this.performedBy != null ? this.performedBy.getId() : null;
    }

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
