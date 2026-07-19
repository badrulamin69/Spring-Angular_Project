package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Column(name = "option_e", length = 500)
    private String optionE;

    @NotBlank
    @Column(name = "correct_option", nullable = false, length = 1)
    private String correctOption;

    @Column(nullable = false)
    private Double marks = 1.0;

    @Column(name = "negative_marks")
    private Double negativeMarks = 0.0;

    @Column(name = "subject", length = 100)
    private String subject;

    @Column(name = "difficulty", length = 20)
    private String difficulty = "MEDIUM";

    @Column(name = "question_type", length = 20)
    private String questionType = "MCQ";

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private AdmissionTest test;

    @JsonProperty("testId")
    public Long getTestId() {
        return test != null ? test.getId() : null;
    }

    @JsonProperty("testId")
    public void setTestId(Long testId) {
        if (testId != null) {
            AdmissionTest t = new AdmissionTest();
            t.setId(testId);
            this.test = t;
        }
    }
}
