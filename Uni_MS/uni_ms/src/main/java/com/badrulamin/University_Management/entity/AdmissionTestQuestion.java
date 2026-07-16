package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admission_test_question")
public class AdmissionTestQuestion extends BaseEntity {

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @NotBlank
    @Column(name = "option_a", nullable = false, length = 500)
    private String optionA;

    @NotBlank
    @Column(name = "option_b", nullable = false, length = 500)
    private String optionB;

    @NotBlank
    @Column(name = "option_c", nullable = false, length = 500)
    private String optionC;

    @NotBlank
    @Column(name = "option_d", nullable = false, length = 500)
    private String optionD;

    @NotBlank
    @Column(name = "correct_option", nullable = false, length = 1)
    private String correctOption;

    @Column(nullable = false)
    private Double marks = 1.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private AdmissionTest test;
}
