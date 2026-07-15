package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

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
