package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "admission_merit_list_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionMeritListEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merit_list_id", nullable = false)
    private AdmissionMeritList meritList;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_id")
    private PreAdmissionRegistration registration;

    @Column(name = "rank_no", nullable = false)
    private Integer rank;

    @Column(name = "roll_number", length = 30)
    private String rollNumber;

    @Column(name = "application_number", length = 50)
    private String applicationNumber;

    @Column(name = "applicant_name", length = 200)
    private String applicantName;

    @Column(name = "faculty_name", length = 200)
    private String facultyName;

    @Column(name = "department_name", length = 200)
    private String departmentName;

    @Column(name = "program_name", length = 200)
    private String programName;

    @Column(name = "shift", length = 20)
    private String shift;

    @Column(name = "test_marks")
    private Double testMarks;

    @Column(name = "test_max_marks")
    private Double testMaxMarks;

    @Column(name = "score")
    private Double score;

    @Column(name = "academic_score")
    private Double academicScore;

    @Column(name = "total_weighted_score")
    private Double totalWeightedScore;

    @Column(name = "ssc_gpa")
    private Double sscGpa;

    @Column(name = "hsc_gpa")
    private Double hscGpa;

    @Column(name = "quota_type", length = 50)
    private String quotaType;

    @Column(name = "status", length = 50)
    private String status = "SELECTED";

    @Column(name = "is_offered")
    private Boolean isOffered = false;

    @Column(name = "is_enrolled")
    private Boolean isEnrolled = false;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("meritListId")
    public Long getMeritListId() { return meritList != null ? meritList.getId() : null; }
    @JsonProperty("meritListId")
    public void setMeritListId(Long meritListId) {
        if (meritListId != null) { AdmissionMeritList m = new AdmissionMeritList(); m.setId(meritListId); this.meritList = m; }
    }

    @JsonProperty("registrationId")
    public Long getRegistrationId() { return registration != null ? registration.getId() : null; }
    @JsonProperty("registrationId")
    public void setRegistrationId(Long registrationId) {
        if (registrationId != null) { PreAdmissionRegistration r = new PreAdmissionRegistration(); r.setId(registrationId); this.registration = r; }
    }
}
