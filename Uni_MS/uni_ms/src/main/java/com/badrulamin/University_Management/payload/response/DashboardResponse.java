package com.badrulamin.University_Management.payload.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DashboardResponse {
    private List<Map<String, Object>> cards;
    private List<Map<String, Object>> charts;
    private List<Map<String, Object>> quickActions;
    private List<Map<String, Object>> recentActivities;
    private Map<String, Object> summary;
}
