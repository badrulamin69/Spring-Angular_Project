package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "admission_waiting_list_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionWaitingListEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "waiting_list_id", nullable = false)
    private AdmissionWaitingList waitingList;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id")
    private PreAdmissionRegistration registration;

    @Column(name = "waiting_rank", nullable = false)
    private Integer rank;

    @Column(name = "roll_number", length = 30)
    private String rollNumber;

    @Column(name = "application_number", length = 50)
    private String applicationNumber;

    @Column(name = "applicant_name", length = 200)
    private String applicantName;

    @Column(name = "score")
    private Double score;

    @Column(name = "test_marks")
    private Double testMarks;

    @Column(name = "total_weighted_score")
    private Double totalWeightedScore;

    @Column(name = "status", length = 50)
    @Builder.Default
    private String status = "WAITING";

    @Column(name = "is_promoted")
    @Builder.Default
    private Boolean isPromoted = false;

    @Column(name = "is_offered")
    @Builder.Default
    private Boolean isOffered = false;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("waitingListId")
    public Long getWaitingListId() { return waitingList != null ? waitingList.getId() : null; }
    @JsonProperty("waitingListId")
    public void setWaitingListId(Long waitingListId) {
        if (waitingListId != null) { AdmissionWaitingList w = new AdmissionWaitingList(); w.setId(waitingListId); this.waitingList = w; }
    }

    @JsonProperty("registrationId")
    public Long getRegistrationId() { return registration != null ? registration.getId() : null; }
    @JsonProperty("registrationId")
    public void setRegistrationId(Long registrationId) {
        if (registrationId != null) { PreAdmissionRegistration r = new PreAdmissionRegistration(); r.setId(registrationId); this.registration = r; }
    }
}
