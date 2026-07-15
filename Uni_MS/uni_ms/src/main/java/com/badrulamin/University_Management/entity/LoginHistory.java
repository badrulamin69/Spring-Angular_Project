package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "login_history")
public class LoginHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String username;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "login_timestamp", nullable = false)
    private LocalDateTime loginTimestamp;

    @Column(nullable = false)
    private Boolean success = false;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "logout_timestamp")
    private LocalDateTime logoutTimestamp;

    @JsonProperty("userId")
    public void setUserId(Long id) {
        if (id != null) {
            this.user = new User();
            this.user.setId(id);
        }
    }

    @JsonProperty
    public Long getUserId() {
        return this.user != null ? this.user.getId() : null;
    }
}

