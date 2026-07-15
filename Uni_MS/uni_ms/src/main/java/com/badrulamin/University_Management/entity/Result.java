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
@Table(name = "results")
public class Result extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @Column(name = "total_marks_obtained", nullable = false)
    private BigDecimal totalMarksObtained;

    @NotNull
    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    private BigDecimal percentage;

    private String grade;

    @Column(name = "result_status")
    private String resultStatus;

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
}
