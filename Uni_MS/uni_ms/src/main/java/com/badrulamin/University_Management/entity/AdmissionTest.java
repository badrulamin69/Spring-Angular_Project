package com.badrulamin.University_Management.entity;

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
@Table(name = "admission_tests")
public class AdmissionTest extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(name = "test_date", nullable = false)
    private LocalDate testDate;

    @NotNull
    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @NotNull
    @Column(name = "passing_marks", nullable = false)
    private Integer passingMarks;

    @Column(columnDefinition = "TEXT")
    private String description;
}
