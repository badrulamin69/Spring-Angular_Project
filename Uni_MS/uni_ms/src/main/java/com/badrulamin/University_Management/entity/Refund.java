package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "refunds")
public class Refund extends BaseEntity {

    @NotBlank
    @Size(max = 30)
    @Column(name = "refund_number", unique = true, nullable = false)
    private String refundNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    private Double amount;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Size(max = 20)
    @Column(nullable = false)
    private String status = "PENDING";

    @Size(max = 100)
    private String approvedBy;

    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @JsonProperty("paymentId")
    public void setPaymentId(Long id) {
        if (id != null) {
            this.payment = new Payment();
            this.payment.setId(id);
        }
    }

    @JsonProperty
    public Long getPaymentId() {
        return this.payment != null ? this.payment.getId() : null;
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
