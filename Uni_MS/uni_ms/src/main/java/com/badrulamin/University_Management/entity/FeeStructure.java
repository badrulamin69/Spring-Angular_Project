package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fee_structures")
public class FeeStructure extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fee_type_id", nullable = false)
    private FeeType feeType;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @NotNull
    @Column(nullable = false)
    private Double amount;

    @Column(name = "due_days")
    private Integer dueDays = 30;

    @Size(max = 20)
    @Column(name = "academic_year")
    private String academicYear;

    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    @JsonProperty("feeTypeId")
    public void setFeeTypeId(Long id) {
        if (id != null) {
            this.feeType = new FeeType();
            this.feeType.setId(id);
        }
    }

    @JsonProperty
    public Long getFeeTypeId() {
        return this.feeType != null ? this.feeType.getId() : null;
    }

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
}
