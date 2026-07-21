package com.badrulamin.University_Management.payload.response;

import lombok.Data;

@Data
public class ConflictCheckResponse {
    private boolean hasConflict;
    private String conflictType;
    private String conflictMessage;
    private Long conflictingRoutineId;
    private String conflictingDetails;
}
