package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentValidationRequest {

    @NotNull(message = "Registration ID is required")
    private Long registrationId;

    @NotNull(message = "Payment reference is required")
    @NotBlank(message = "Payment reference is required")
    private String paymentReference;

    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be positive")
    private Double paymentAmount;
}
