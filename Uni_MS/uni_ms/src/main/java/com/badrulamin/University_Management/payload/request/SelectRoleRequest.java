package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SelectRoleRequest {

    @NotBlank(message = "Role code is required")
    private String roleCode;
}
