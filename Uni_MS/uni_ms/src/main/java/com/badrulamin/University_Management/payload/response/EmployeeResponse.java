package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String designation;
    private Long departmentId;
    private String departmentName;
    private LocalDate joiningDate;
    private BigDecimal salary;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
