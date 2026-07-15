package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exams")
public class Exam extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(name = "exam_type", nullable = false)
    private String examType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @NotNull
    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @NotNull
    @Column(name = "passing_marks", nullable = false)
    private Integer passingMarks;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    private String description;

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

    @JsonProperty("subjectId")
    public void setSubjectId(Long id) {
        if (id != null) {
            this.subject = new Subject();
            this.subject.setId(id);
        }
    }

    @JsonProperty
    public Long getSubjectId() {
        return this.subject != null ? this.subject.getId() : null;
    }
}
