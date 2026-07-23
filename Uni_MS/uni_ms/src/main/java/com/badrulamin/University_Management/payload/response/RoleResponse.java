package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Boolean active;
    private Integer level;
    private Long parentRoleId;
    private String parentRoleName;
    private List<String> permissionCodes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
