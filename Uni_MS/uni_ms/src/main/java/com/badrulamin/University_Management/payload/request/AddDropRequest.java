package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddDropRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    private List<Long> addSubjectIds;

    private List<Long> dropRegistrationIds;

    private String remarks;
}
