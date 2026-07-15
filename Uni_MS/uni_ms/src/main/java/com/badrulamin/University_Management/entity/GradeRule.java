package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grade_rules")
public class GradeRule extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotBlank
    @Column(nullable = false)
    private String grade;

    @NotNull
    @Column(name = "min_percentage", nullable = false)
    private BigDecimal minPercentage;

    @NotNull
    @Column(name = "max_percentage", nullable = false)
    private BigDecimal maxPercentage;

    @NotNull
    @Column(name = "grade_point", nullable = false)
    private BigDecimal gradePoint;

    private String description;

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
}
