package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "marks")
public class Mark extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @NotNull
    @Column(name = "marks_obtained", nullable = false)
    private BigDecimal marksObtained;

    @NotNull
    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    private String grade;

    private String remarks;

    @JsonProperty("examId")
    public void setExamId(Long id) {
        if (id != null) {
            this.exam = new Exam();
            this.exam.setId(id);
        }
    }

    @JsonProperty
    public Long getExamId() {
        return this.exam != null ? this.exam.getId() : null;
    }

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
