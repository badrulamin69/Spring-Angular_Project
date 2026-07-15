package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    private String subject;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    private Boolean isRead = false;

    private LocalDateTime readAt;

    @JsonProperty("senderId")
    public void setSenderId(Long id) {
        if (id != null) {
            this.sender = new User();
            this.sender.setId(id);
        }
    }

    @JsonProperty
    public Long getSenderId() {
        return this.sender != null ? this.sender.getId() : null;
    }

    @JsonProperty("receiverId")
    public void setReceiverId(Long id) {
        if (id != null) {
            this.receiver = new User();
            this.receiver.setId(id);
        }
    }

    @JsonProperty
    public Long getReceiverId() {
        return this.receiver != null ? this.receiver.getId() : null;
    }
}
