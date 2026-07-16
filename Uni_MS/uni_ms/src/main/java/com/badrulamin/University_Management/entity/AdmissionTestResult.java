package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admission_test_result")
public class AdmissionTestResult extends BaseEntity {

    @Column(name = "written_marks")
    private Double writtenMarks;

    @Column(name = "mcq_marks")
    private Double mcqMarks;

    @Column(name = "viva_marks")
    private Double vivaMarks;

    @Column(name = "written_max")
    private Double writtenMax = 100.0;

    @Column(name = "mcq_max")
    private Double mcqMax = 100.0;

    @Column(name = "viva_max")
    private Double vivaMax = 50.0;

    @Column(name = "total_weighted_score")
    private Double totalWeightedScore;

    @Size(max = 50)
    private String status = "PENDING";

    @Size(max = 1000)
    private String remarks;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id", nullable = false)
    private PreAdmissionRegistration registration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id")
    private AdmissionTest test;

    @JsonProperty("registrationId")
    public Long getRegistrationId() {
        return registration != null ? registration.getId() : null;
    }

    @JsonProperty("registrationId")
    public void setRegistrationId(Long registrationId) {
        if (registrationId != null) {
            PreAdmissionRegistration r = new PreAdmissionRegistration();
            r.setId(registrationId);
            this.registration = r;
        }
    }

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

    @PrePersist
    @PreUpdate
    public void calculateWeightedScore() {
        double w = (writtenMarks != null ? writtenMarks : 0.0);
        double m = (mcqMarks != null ? mcqMarks : 0.0);
        double v = (vivaMarks != null ? vivaMarks : 0.0);
        double wMax = (writtenMax != null && writtenMax > 0) ? writtenMax : 100.0;
        double mMax = (mcqMax != null && mcqMax > 0) ? mcqMax : 100.0;
        double vMax = (vivaMax != null && vivaMax > 0) ? vivaMax : 50.0;
        this.totalWeightedScore = (w / wMax) * 40 + (m / mMax) * 30 + (v / vMax) * 30;
    }
}
