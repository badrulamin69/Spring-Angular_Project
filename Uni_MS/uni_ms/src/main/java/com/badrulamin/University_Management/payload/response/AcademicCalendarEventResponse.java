package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AcademicCalendarEventResponse {
    private Long id;
    private String title;
    private String description;
    private String eventType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String startTime;
    private String endTime;
    private Long semesterId;
    private String semesterName;
    private Long academicSessionId;
    private String academicSessionName;
    private boolean isHoliday;
    private boolean isPublished;
    private boolean isAllDay;
    private String color;
    private String location;
    private String recurrence;
    private boolean notifyStudents;
    private boolean notifyTeachers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
