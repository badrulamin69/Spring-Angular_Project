package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatar;
    private Boolean active;
    private Boolean emailVerified;
    private String roleCode;
    private String roleName;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
