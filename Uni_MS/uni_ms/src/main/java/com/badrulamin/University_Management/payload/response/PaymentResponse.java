package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private String paymentNumber;
    private Long invoiceId;
    private Long studentId;
    private String studentName;
    private Double amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String notes;
    private LocalDateTime createdAt;
}
