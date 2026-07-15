package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "curricula", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"program_id", "subject_id"})
})
public class Curriculum extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @JsonProperty("programId")
    public void setProgramId(Long id) {
        if (id != null) {
            this.program = new Program();
            this.program.setId(id);
        }
    }

    @JsonProperty
    public Long getProgramId() {
        return this.program != null ? this.program.getId() : null;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

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

    @NotNull
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

    @Column(nullable = false)
    private boolean isRequired = true;

    private Integer orderNo;

    private Integer creditHours;
}
