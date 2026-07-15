package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "admission_merit_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionMeritList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private AcademicSession session;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id")
    private Program program;

    @Column(nullable = false)
    private String status;

    private Integer totalSeats;

    private Double cutoffScore;

    @Column(name = "`rank`")
    private Integer rank;

    private Double score;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id")
    private AdmissionApplication application;

    @Column(length = 1000)
    private String remarks;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

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

    @JsonProperty("programId")
    public Long getProgramId() {
        return program != null ? program.getId() : null;
    }

    @JsonProperty("programId")
    public void setProgramId(Long programId) {
        if (programId != null) {
            Program p = new Program();
            p.setId(programId);
            this.program = p;
        }
    }

    @JsonProperty("applicationId")
    public Long getApplicationId() {
        return application != null ? application.getId() : null;
    }

    @JsonProperty("applicationId")
    public void setApplicationId(Long applicationId) {
        if (applicationId != null) {
            AdmissionApplication a = new AdmissionApplication();
            a.setId(applicationId);
            this.application = a;
        }
    }
}
