package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentValidationRequest {

    @NotNull(message = "Registration ID is required")
    private Long registrationId;

    @NotNull(message = "Payment reference is required")
    private String paymentReference;

    @NotNull(message = "Payment amount is required")
    private Double paymentAmount;
}
