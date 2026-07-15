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
@Table(name = "semester_routines")
public class SemesterRoutine extends BaseEntity {

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
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

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

    private String description;

    @Column(name = "total_weeks")
    private Integer totalWeeks = 16;

    @Column(name = "midterm_week")
    private Integer midtermWeek = 8;

    @Column(name = "final_exam_week")
    private Integer finalExamWeek = 16;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
