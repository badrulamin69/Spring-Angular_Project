package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
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
@Table(name = "fines")
public class Fine extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fee_type_id")
    private FeeType feeType;

    @NotNull
    @Column(nullable = false)
    private Double amount;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Size(max = 100)
    private String issuedBy;

    @Size(max = 20)
    @Column(nullable = false)
    private String status = "PENDING";

    private LocalDate issuedDate;

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

    @JsonProperty("invoiceId")
    public void setInvoiceId(Long id) {
        if (id != null) {
            this.invoice = new Invoice();
            this.invoice.setId(id);
        }
    }

    @JsonProperty
    public Long getInvoiceId() {
        return this.invoice != null ? this.invoice.getId() : null;
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
