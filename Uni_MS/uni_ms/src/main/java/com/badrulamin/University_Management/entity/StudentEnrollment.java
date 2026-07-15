package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_enrollments")
public class StudentEnrollment extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    @NotNull
    @Column(nullable = false)
    private LocalDate enrollmentDate;

    private String status;

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

    @JsonProperty("batchId")
    public void setBatchId(Long id) {
        if (id != null) {
            this.batch = new Batch();
            this.batch.setId(id);
        }
    }

    @JsonProperty
    public Long getBatchId() {
        return this.batch != null ? this.batch.getId() : null;
    }

    @JsonProperty("sectionId")
    public void setSectionId(Long id) {
        if (id != null) {
            this.section = new Section();
            this.section.setId(id);
        }
    }

    @JsonProperty
    public Long getSectionId() {
        return this.section != null ? this.section.getId() : null;
    }
}
