package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.util.List;

@Data
public class RoutineGenerateResponse {
    private int totalGenerated;
    private int conflictsFound;
    private List<ClassRoutineResponse> generatedRoutines;
    private List<ConflictCheckResponse> conflicts;
}
