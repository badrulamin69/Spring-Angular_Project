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
@Table(name = "seat_allocations")
public class SeatAllocation extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id", nullable = false)
    private AdmissionTest test;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id", nullable = false)
    private PreAdmissionRegistration registration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "center_id")
    private ExamCenter center;

    @Column(name = "center_name", length = 200)
    private String centerName;

    @Column(name = "building_name", length = 200)
    private String buildingName;

    @Column(name = "room_name", length = 100)
    private String roomName;

    @NotBlank
    @Column(name = "seat_number", nullable = false, length = 30)
    private String seatNumber;

    @NotBlank
    @Column(name = "roll_number", nullable = false, length = 30)
    private String rollNumber;

    @Column(name = "status", length = 30)
    private String status = "ASSIGNED";

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

    @JsonProperty("centerId")
    public Long getCenterId() {
        return center != null ? center.getId() : null;
    }

    @JsonProperty("centerId")
    public void setCenterId(Long centerId) {
        if (centerId != null) {
            ExamCenter c = new ExamCenter();
            c.setId(centerId);
            this.center = c;
        }
    }
}
