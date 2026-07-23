package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentRefundRequest {
    @NotNull(message = "Refund amount is required")
    @Positive(message = "Refund amount must be positive")
    private Double amount;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String approvedBy;
}
