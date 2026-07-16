package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admission_test_attempt")
public class AdmissionTestAttempt extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", nullable = false)
    private PreAdmissionRegistration registration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private AdmissionTest test;

    @Column(name = "answers", columnDefinition = "TEXT")
    private String answers;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "correct_answers")
    private Integer correctAnswers;

    @Column(name = "score")
    private Double score;

    @Column(name = "max_score")
    private Double maxScore;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "PENDING";
}
