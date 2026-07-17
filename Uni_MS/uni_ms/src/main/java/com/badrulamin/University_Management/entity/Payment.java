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

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @NotBlank
    @Size(max = 30)
    @Column(name = "payment_number", unique = true, nullable = false)
    private String paymentNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @Column(nullable = false)
    private Double amount;

    @Size(max = 30)
    @Column(name = "payment_method")
    private String paymentMethod;

    @Size(max = 20)
    @Column(name = "payment_status")
    private String paymentStatus = "PENDING";

    @Size(max = 100)
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    private LocalDateTime paymentDate;

    @Size(max = 100)
    private String createdBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Size(max = 500)
    @Column(name = "receipt_url")
    private String receiptUrl;

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
