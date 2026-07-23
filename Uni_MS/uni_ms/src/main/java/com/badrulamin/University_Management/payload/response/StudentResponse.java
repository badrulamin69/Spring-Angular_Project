package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudentResponse {
    private Long id;
    private String studentCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private LocalDate enrollmentDate;
    private String status;
    private Long departmentId;
    private String departmentName;
    private LocalDateTime createdAt;
}
