package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {
    @NotBlank(message = "Role name is required")
    private String name;

    @NotBlank(message = "Role code is required")
    private String code;

    private String description;

    private Boolean active = true;

    private Integer level = 0;

    private Long parentRoleId;
}
