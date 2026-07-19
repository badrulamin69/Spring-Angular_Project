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
@Table(name = "admission_attendance")
public class AdmissionAttendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id", nullable = false)
    private AdmissionTest test;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id", nullable = false)
    private PreAdmissionRegistration registration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private AdmissionTestAttempt attempt;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ABSENT";

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by")
    private User markedBy;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "qr_scanned")
    private Boolean qrScanned = false;

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

    @JsonProperty("attemptId")
    public Long getAttemptId() {
        return attempt != null ? attempt.getId() : null;
    }

    @JsonProperty("attemptId")
    public void setAttemptId(Long attemptId) {
        if (attemptId != null) {
            AdmissionTestAttempt a = new AdmissionTestAttempt();
            a.setId(attemptId);
            this.attempt = a;
        }
    }

    @JsonProperty("markedById")
    public Long getMarkedById() {
        return markedBy != null ? markedBy.getId() : null;
    }

    @JsonProperty("markedById")
    public void setMarkedById(Long markedById) {
        if (markedById != null) {
            User u = new User();
            u.setId(markedById);
            this.markedBy = u;
        }
    }
}
