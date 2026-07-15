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

    @Column(name = "class_type")
    private String classType = "Lecture";

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
