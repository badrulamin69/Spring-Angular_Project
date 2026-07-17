package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice_items")
public class InvoiceItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fee_type_id")
    private FeeType feeType;

    private String description;

    private Double amount;

    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;

    @Column(name = "net_amount")
    private Double netAmount;

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
