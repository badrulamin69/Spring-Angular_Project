package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "payment_transactions")
public class PaymentTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @NotBlank
    @Size(max = 50)
    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;

    @Size(max = 100)
    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    private Double amount;

    @Size(max = 20)
    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Size(max = 50)
    @Column(name = "gateway_name")
    private String gatewayName;

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
}
