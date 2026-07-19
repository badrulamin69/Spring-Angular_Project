package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private AcademicSession session;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id")
    private Program program;

    @Column(name = "shift", length = 20)
    private String shift;

    @Column(name = "test_type", length = 30)
    private String testType = "MCQ";

    @NotNull
    @Column(name = "test_date", nullable = false)
    private LocalDate testDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @NotNull
    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @NotNull
    @Column(name = "passing_marks", nullable = false)
    private Integer passingMarks;

    @Column(name = "negative_marking")
    private Boolean negativeMarking = false;

    @Column(name = "negative_mark_value")
    private Double negativeMarkValue = 0.0;

    @Column(name = "exam_center", length = 200)
    private String examCenter;

    @Column(name = "building", length = 200)
    private String building;

    @Column(name = "room", length = 100)
    private String room;

    @Column(name = "seat_capacity")
    private Integer seatCapacity;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 30)
    @Column(nullable = false)
    private String status = "DRAFT";

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

    @JsonProperty("facultyId")
    public Long getFacultyId() {
        return faculty != null ? faculty.getId() : null;
    }

    @JsonProperty("facultyId")
    public void setFacultyId(Long facultyId) {
        if (facultyId != null) {
            Faculty f = new Faculty();
            f.setId(facultyId);
            this.faculty = f;
        }
    }

    @JsonProperty("departmentId")
    public Long getDepartmentId() {
        return department != null ? department.getId() : null;
    }

    @JsonProperty("departmentId")
    public void setDepartmentId(Long departmentId) {
        if (departmentId != null) {
            Department d = new Department();
            d.setId(departmentId);
            this.department = d;
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
}
