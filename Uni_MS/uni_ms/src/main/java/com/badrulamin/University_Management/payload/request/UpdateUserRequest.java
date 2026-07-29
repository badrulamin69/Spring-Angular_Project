package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateUserRequest {
    @Size(min = 3, max = 50)
    private String username;

    @Email
    private String email;

    @Size(min = 8, max = 128)
    private String password;

    private String firstName;
    private String lastName;
    private String phone;
    private Boolean active;
    private Set<String> roleCodes;
}
