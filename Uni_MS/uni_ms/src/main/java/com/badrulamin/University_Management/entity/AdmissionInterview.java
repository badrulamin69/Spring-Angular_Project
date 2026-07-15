package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "admission_interview")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionInterview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    private AdmissionApplication application;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interviewer_id")
    private User interviewer;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    private LocalDateTime completedAt;

    @Column(length = 50)
    private String interviewType;

    @Column(length = 50)
    private String status;

    @Column(length = 2000)
    private String remarks;

    private Integer score;

    private Integer maxScore;

    @Column(length = 2000)
    private String strengths;

    @Column(length = 2000)
    private String weaknesses;

    private Boolean isRecommended;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("applicationId")
    public Long getApplicationId() {
        return application != null ? application.getId() : null;
    }

    @JsonProperty("applicationId")
    public void setApplicationId(Long applicationId) {
        if (applicationId != null) {
            AdmissionApplication a = new AdmissionApplication();
            a.setId(applicationId);
            this.application = a;
        }
    }

    @JsonProperty("interviewerId")
    public Long getInterviewerId() {
        return interviewer != null ? interviewer.getId() : null;
    }

    @JsonProperty("interviewerId")
    public void setInterviewerId(Long interviewerId) {
        if (interviewerId != null) {
            User u = new User();
            u.setId(interviewerId);
            this.interviewer = u;
        }
    }
}
