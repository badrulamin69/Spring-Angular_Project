package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "discounts")
public class Discount extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fee_type_id")
    private FeeType feeType;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false)
    private String discountType;

    @NotNull
    @Column(nullable = false)
    private Double discountValue;

    private String description;

    private LocalDate validFrom;

    private LocalDate validTo;

    @Column(nullable = false)
    private Boolean isActive = true;

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
