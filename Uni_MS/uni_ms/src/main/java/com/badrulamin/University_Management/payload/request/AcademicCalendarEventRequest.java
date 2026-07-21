package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AcademicCalendarEventRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Event type is required")
    private String eventType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private String startTime;

    private String endTime;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    private Long academicSessionId;

    private boolean isHoliday = false;

    private boolean isPublished = false;

    private boolean isAllDay = true;

    private String color;

    private String location;

    private String recurrence;

    private boolean notifyStudents = false;

    private boolean notifyTeachers = false;
}
