package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "class_routines")
public class ClassRoutine extends BaseEntity {

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
    @JoinColumn(name = "administration_id", nullable = false)
    private Administration administration;

    @JsonProperty("administrationId")
    public void setAdministrationId(Long id) {
        if (id != null) {
            this.administration = new Administration();
            this.administration.setId(id);
        }
    }

    @JsonProperty
    public Long getAdministrationId() {
        return this.administration != null ? this.administration.getId() : null;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
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

    @NotBlank
    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    @NotBlank
    @Column(name = "start_time", nullable = false)
    private String startTime;

    @NotBlank
    @Column(name = "end_time", nullable = false)
    private String endTime;

    private String room;

    private String building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id")
    private TimeSlot timeSlot;

    @JsonProperty("timeSlotId")
    public void setTimeSlotId(Long id) {
        if (id != null) {
            this.timeSlot = new TimeSlot();
            this.timeSlot.setId(id);
        }
    }

    @JsonProperty
    public Long getTimeSlotId() {
        return this.timeSlot != null ? this.timeSlot.getId() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @JsonProperty("classroomId")
    public void setClassroomId(Long id) {
        if (id != null) {
            this.classroom = new Classroom();
            this.classroom.setId(id);
        }
    }

    @JsonProperty
    public Long getClassroomId() {
        return this.classroom != null ? this.classroom.getId() : null;
    }

    @Column(name = "class_type")
    private String classType = "Lecture";

    @Column(name = "shift")
    private String shift;

    @Column(name = "publish_status")
    private String publishStatus = "DRAFT";

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
