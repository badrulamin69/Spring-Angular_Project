package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "academic_calendar_events")
public class AcademicCalendarEvent extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String title;

    private String description;

    @NotBlank
    @Column(nullable = false)
    private String eventType;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 5)
    private String startTime;

    @Column(length = 5)
    private String endTime;

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
    @JoinColumn(name = "academic_session_id")
    private AcademicSession academicSession;

    @JsonProperty("academicSessionId")
    public void setAcademicSessionId(Long id) {
        if (id != null) {
            this.academicSession = new AcademicSession();
            this.academicSession.setId(id);
        }
    }

    @JsonProperty
    public Long getAcademicSessionId() {
        return this.academicSession != null ? this.academicSession.getId() : null;
    }

    @Column(nullable = false)
    private boolean isHoliday = false;

    @Column(nullable = false)
    private boolean isPublished = false;

    @Column(nullable = false)
    private boolean isAllDay = true;

    private String color;

    private String location;

    private String recurrence;

    @Column(nullable = false)
    private boolean notifyStudents = false;

    @Column(nullable = false)
    private boolean notifyTeachers = false;
}
