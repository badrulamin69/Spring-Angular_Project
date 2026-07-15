package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assignments")
public class Assignment extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String title;

    private String description;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @NotNull
    @Column(name = "max_marks", nullable = false)
    private Integer maxMarks;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administration_id", nullable = false)
    private Administration administration;

    @JsonProperty("courseId")
    public void setCourseId(Long id) {
        if (id != null) {
            this.course = new Course();
            this.course.setId(id);
        }
    }

    @JsonProperty
    public Long getCourseId() {
        return this.course != null ? this.course.getId() : null;
    }

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
}
