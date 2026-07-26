package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "student_attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAttendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    @Column(nullable = false)
    private String status;

    @Column(length = 200)
    private String remarks;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recorded_by")
    private User recordedBy;

    @JsonProperty("studentId")
    public Long getStudentId() { return student != null ? student.getId() : null; }

    @JsonProperty("studentId")
    public void setStudentId(Long studentId) {
        if (studentId != null) { Student s = new Student(); s.setId(studentId); this.student = s; }
    }

    @JsonProperty("courseId")
    public Long getCourseId() { return course != null ? course.getId() : null; }

    @JsonProperty("courseId")
    public void setCourseId(Long courseId) {
        if (courseId != null) { Course c = new Course(); c.setId(courseId); this.course = c; }
    }

    @JsonProperty("semesterId")
    public Long getSemesterId() { return semester != null ? semester.getId() : null; }

    @JsonProperty("semesterId")
    public void setSemesterId(Long semesterId) {
        if (semesterId != null) { Semester s = new Semester(); s.setId(semesterId); this.semester = s; }
    }

    @JsonProperty("recordedById")
    public Long getRecordedById() { return recordedBy != null ? recordedBy.getId() : null; }

    @JsonProperty("recordedById")
    public void setRecordedById(Long recordedById) {
        if (recordedById != null) { User u = new User(); u.setId(recordedById); this.recordedBy = u; }
    }
}
