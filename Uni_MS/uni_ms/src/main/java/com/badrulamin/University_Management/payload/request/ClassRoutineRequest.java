package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassRoutineRequest {
    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Administration ID is required")
    private Long administrationId;

    @NotNull(message = "Section ID is required")
    private Long sectionId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    private Long timeSlotId;

    private Long classroomId;

    @NotBlank(message = "Day of week is required")
    private String dayOfWeek;

    @NotBlank(message = "Start time is required")
    private String startTime;

    @NotBlank(message = "End time is required")
    private String endTime;

    private String room;

    private String building;

    private String classType = "Lecture";

    private String shift;

    private boolean isActive = true;
}
