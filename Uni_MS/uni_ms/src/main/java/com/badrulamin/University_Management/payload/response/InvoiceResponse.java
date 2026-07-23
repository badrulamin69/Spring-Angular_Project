package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long studentId;
    private String studentName;
    private String academicYear;
    private Long semesterId;
    private String semesterName;
    private Double totalAmount;
    private Double paidAmount;
    private Double dueAmount;
    private Double discountAmount;
    private Double fineAmount;
    private String status;
    private LocalDate dueDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
