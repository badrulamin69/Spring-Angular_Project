package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClassRoutineResponse {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Long administrationId;
    private String teacherName;
    private Long sectionId;
    private String sectionName;
    private Long semesterId;
    private String semesterName;
    private Long batchId;
    private String batchName;
    private Long timeSlotId;
    private String timeSlotName;
    private Long classroomId;
    private String classroomNumber;
    private String buildingName;
    private String departmentName;
    private String programName;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String room;
    private String building;
    private String classType;
    private String shift;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
