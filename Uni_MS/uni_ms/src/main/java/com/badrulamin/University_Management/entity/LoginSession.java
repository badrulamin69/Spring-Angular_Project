package com.badrulamin.University_Management.entity;

import com.badrulamin.University_Management.entity.BaseEntity;
import com.badrulamin.University_Management.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_token", unique = true, nullable = false)
    private String sessionToken;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "browser")
    private String browser;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "last_activity_time")
    private LocalDateTime lastActivityTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "expired")
    private boolean expired = false;

    @JsonProperty("userId")
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    @JsonProperty("userId")
    public void setUserId(Long userId) {
        if (this.user == null) {
            this.user = new User();
        }
        this.user.setId(userId);
    }
}
