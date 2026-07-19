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
@Table(name = "applicant_choice_submissions")
public class ApplicantChoiceSubmission extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id", nullable = false)
    private PreAdmissionRegistration registration;

    @JsonProperty("registrationId")
    public Long getRegistrationId() { return registration != null ? registration.getId() : null; }
    @JsonProperty("registrationId")
    public void setRegistrationId(Long registrationId) {
        if (registrationId != null) {
            PreAdmissionRegistration r = new PreAdmissionRegistration();
            r.setId(registrationId);
            this.registration = r;
        }
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "config_id", nullable = false)
    private ChoiceFillingConfig config;

    @JsonProperty("configId")
    public Long getConfigId() { return config != null ? config.getId() : null; }
    @JsonProperty("configId")
    public void setConfigId(Long configId) {
        if (configId != null) {
            ChoiceFillingConfig c = new ChoiceFillingConfig();
            c.setId(configId);
            this.config = c;
        }
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merit_list_entry_id")
    private AdmissionMeritListEntry meritListEntry;

    @JsonProperty("meritListEntryId")
    public Long getMeritListEntryId() { return meritListEntry != null ? meritListEntry.getId() : null; }
    @JsonProperty("meritListEntryId")
    public void setMeritListEntryId(Long meritListEntryId) {
        if (meritListEntryId != null) {
            AdmissionMeritListEntry e = new AdmissionMeritListEntry();
            e.setId(meritListEntryId);
            this.meritListEntry = e;
        }
    }

    @Column(name = "submission_id", unique = true, length = 50)
    private String submissionId;

    @Column(name = "total_choices", nullable = false)
    private Integer totalChoices = 0;

    @Column(nullable = false, length = 30)
    private String status = "DRAFT";

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "applicant_name", length = 200)
    private String applicantName;

    @Column(name = "merit_rank")
    private Integer meritRank;

    @Column(name = "merit_score")
    private Double meritScore;
}
