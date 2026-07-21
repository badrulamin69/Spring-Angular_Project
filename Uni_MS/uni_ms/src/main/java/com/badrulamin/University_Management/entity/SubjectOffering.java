package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "subject_offerings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @JsonProperty("subjectId")
    public void setSubjectId(Long id) {
        if (id != null) {
            this.subject = new Subject();
            this.subject.setId(id);
        }
    }

    @JsonProperty
    public Long getSubjectId() {
        return this.subject != null ? this.subject.getId() : null;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @JsonProperty("semesterId")
    public void setSemesterId(Long id) {
        if (id != null) {
            this.semester = new Semester();
            this.semester.setId(id);
        }
    }

    @JsonProperty
    public Long getSemesterId() {
        return this.semester != null ? this.semester.getId() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @JsonProperty("batchId")
    public void setBatchId(Long id) {
        if (id != null) {
            this.batch = new Batch();
            this.batch.setId(id);
        }
    }

    @JsonProperty
    public Long getBatchId() {
        return this.batch != null ? this.batch.getId() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    @JsonProperty("sectionId")
    public void setSectionId(Long id) {
        if (id != null) {
            this.section = new Section();
            this.section.setId(id);
        }
    }

    @JsonProperty
    public Long getSectionId() {
        return this.section != null ? this.section.getId() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @JsonProperty("facultyId")
    public void setFacultyId(Long id) {
        if (id != null) {
            this.faculty = new Faculty();
            this.faculty.setId(id);
        }
    }

    @JsonProperty
    public Long getFacultyId() {
        return this.faculty != null ? this.faculty.getId() : null;
    }

    @Column(name = "day_of_week", length = 10)
    private String dayOfWeek;

    @Column(name = "start_time", length = 5)
    private String startTime;

    @Column(name = "end_time", length = 5)
    private String endTime;

    @Column(name = "room_number", length = 50)
    private String roomNumber;

    @Column(name = "max_seats", nullable = false)
    private Integer maxSeats = 40;

    @Column(name = "enrolled_count", nullable = false)
    private Integer enrolledCount = 0;

    @Column(name = "waitlist_count", nullable = false)
    private Integer waitlistCount = 0;

    @Column(name = "max_waitlist")
    private Integer maxWaitlist = 10;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(length = 20)
    private String status = "ACTIVE";

    @Column(length = 500)
    private String remarks;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public boolean hasAvailableSeats() {
        return enrolledCount < maxSeats;
    }

    public boolean hasWaitlistSpace() {
        return waitlistCount < maxWaitlist;
    }
}
