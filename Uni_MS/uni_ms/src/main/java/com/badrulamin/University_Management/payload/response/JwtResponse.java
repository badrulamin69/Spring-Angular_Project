package com.badrulamin.University_Management.payload.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;
    private String roleCode;
    private String roleName;
    private List<String> roles;
    private List<String> permissions;
    private List<Map<String, Object>> menus;

    public JwtResponse(String token, String refreshToken, Long id, String username, String email,
                       String firstName, String lastName, String avatar, String roleCode, String roleName,
                       List<String> roles, List<String> permissions, List<Map<String, Object>> menus) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.roles = roles;
        this.permissions = permissions;
        this.menus = menus;
    }
}
