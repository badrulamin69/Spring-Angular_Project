package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "eligibility_verifications")
public class EligibilityVerification extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id", nullable = false)
    private PreAdmissionRegistration registration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id", nullable = false)
    private AdmissionTest test;

    @Size(max = 30)
    @Column(nullable = false)
    private String status = "PENDING";

    @Size(max = 100)
    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "ssc_gpa_verified")
    private Boolean sscGpaVerified;

    @Column(name = "hsc_gpa_verified")
    private Boolean hscGpaVerified;

    @Column(name = "documents_verified")
    private Boolean documentsVerified;

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
}
