package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
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
@Table(name = "prerequisites", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"subject_id", "prerequisite_subject_id"})
})
public class Prerequisite extends BaseEntity {

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
    @JoinColumn(name = "prerequisite_subject_id", nullable = false)
    private Subject prerequisiteSubject;

    @JsonProperty("prerequisiteSubjectId")
    public void setPrerequisiteSubjectId(Long id) {
        if (id != null) {
            this.prerequisiteSubject = new Subject();
            this.prerequisiteSubject.setId(id);
        }
    }

    @JsonProperty
    public Long getPrerequisiteSubjectId() {
        return this.prerequisiteSubject != null ? this.prerequisiteSubject.getId() : null;
    }

    @Column(name = "min_grade", length = 2)
    private String minGrade = "D";

    @Column(name = "is_mandatory", nullable = false)
    private boolean isMandatory = true;
}
