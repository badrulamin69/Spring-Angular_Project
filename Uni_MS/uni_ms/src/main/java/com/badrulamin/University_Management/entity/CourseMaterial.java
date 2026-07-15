package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_materials")
public class CourseMaterial extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String title;

    private String description;

    @NotBlank
    @Column(name = "material_type", nullable = false)
    private String materialType;

    @Column(name = "file_url")
    private String fileUrl;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

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
