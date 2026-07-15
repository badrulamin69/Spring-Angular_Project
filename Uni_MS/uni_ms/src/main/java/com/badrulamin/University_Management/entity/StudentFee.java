package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_fees")
public class StudentFee extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_type_id", nullable = false)
    private FeeType feeType;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount;

    private String status;

    @Column(name = "academic_year")
    private String academicYear;

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
}
