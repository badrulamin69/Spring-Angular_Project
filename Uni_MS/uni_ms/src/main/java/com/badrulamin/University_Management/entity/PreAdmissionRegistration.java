package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pre_admission_registration")
public class PreAdmissionRegistration extends BaseEntity {

    @Column(name = "registration_number", unique = true, nullable = false)
    private String registrationNumber;

    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Size(max = 20)
    private String phone;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Size(max = 20)
    private String gender;

    @Size(max = 10)
    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Size(max = 500)
    @Column(name = "photo_url")
    private String photoUrl;

    @Size(max = 500)
    @Column(name = "signature_url")
    private String signatureUrl;

    @Size(max = 100)
    @Column(name = "father_name")
    private String fatherName;

    @Size(max = 100)
    @Column(name = "mother_name")
    private String motherName;

    @Size(max = 20)
    @Column(name = "guardian_phone")
    private String guardianPhone;

    @Column(name = "ssc_gpa")
    private Double sscGpa;

    @Column(name = "ssc_year")
    private Integer sscYear;

    @Size(max = 100)
    @Column(name = "ssc_board")
    private String sscBoard;

    @Column(name = "hsc_gpa")
    private Double hscGpa;

    @Column(name = "hsc_year")
    private Integer hscYear;

    @Size(max = 100)
    @Column(name = "hsc_board")
    private String hscBoard;

    @NotBlank
    @Size(max = 100)
    @Column(name = "program_preference1", nullable = false)
    private String programPreference1;

    @Size(max = 100)
    @Column(name = "program_preference2")
    private String programPreference2;

    @Size(max = 100)
    @Column(name = "program_preference3")
    private String programPreference3;

    @Size(max = 50)
    @Column(nullable = false)
    private String status = "DRAFT";

    @Size(max = 2000)
    private String remarks;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private AcademicSession session;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "circular_id")
    private AdmissionCircular circular;

    @JsonProperty("sessionId")
    public Long getSessionId() {
        return session != null ? session.getId() : null;
    }

    @JsonProperty("sessionId")
    public void setSessionId(Long sessionId) {
        if (sessionId != null) {
            AcademicSession s = new AcademicSession();
            s.setId(sessionId);
            this.session = s;
        }
    }

    @JsonProperty("circularId")
    public Long getCircularId() {
        return circular != null ? circular.getId() : null;
    }

    @JsonProperty("circularId")
    public void setCircularId(Long circularId) {
        if (circularId != null) {
            AdmissionCircular c = new AdmissionCircular();
            c.setId(circularId);
            this.circular = c;
        }
    }
}
