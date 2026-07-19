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
@Table(name = "admit_cards")
public class AdmitCard extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id", nullable = false)
    private PreAdmissionRegistration registration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id", nullable = false)
    private AdmissionTest test;

    @Column(name = "admit_card_number", unique = true, nullable = false, length = 50)
    private String admitCardNumber;

    @Column(name = "roll_number", nullable = false, length = 30)
    private String rollNumber;

    @Column(name = "seat_number", length = 30)
    private String seatNumber;

    @Column(name = "center_name", length = 200)
    private String centerName;

    @Column(name = "building_name", length = 200)
    private String buildingName;

    @Column(name = "room_name", length = 100)
    private String roomName;

    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "status", length = 30)
    private String status = "GENERATED";

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
