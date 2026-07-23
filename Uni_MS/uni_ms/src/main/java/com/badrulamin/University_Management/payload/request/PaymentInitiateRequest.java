package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentInitiateRequest {
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(CASH|BANK_TRANSFER|CARD|MOBILE_BANKING|ONLINE)$",
             message = "Payment method must be one of: CASH, BANK_TRANSFER, CARD, MOBILE_BANKING, ONLINE")
    private String paymentMethod;

    private String notes;
}
