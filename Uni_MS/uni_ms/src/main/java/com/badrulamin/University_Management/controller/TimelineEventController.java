package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.TimelineEvent;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.TimelineEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.badrulamin.University_Management.config.PaginationConfig;

import java.util.Map;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineEventController {

    private final TimelineEventService timelineService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<TimelineEvent>>> getTimeline(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        page = PaginationConfig.clampPage(page);
        size = PaginationConfig.clampSize(size);
        return ResponseEntity.ok(ApiResponse.success(
            timelineService.getTimeline(entityType, entityId, PageRequest.of(page, size))));
    }

    @GetMapping("/recent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<TimelineEvent>>> getRecentTimeline(
            @RequestParam String entityType,
            @RequestParam Long entityId) {
        return ResponseEntity.ok(ApiResponse.success(
            timelineService.getRecentTimeline(entityType, entityId)));
    }

    @GetMapping("/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countEvents(
            @RequestParam String entityType,
            @RequestParam Long entityId) {
        return ResponseEntity.ok(ApiResponse.success(
            Map.of("count", timelineService.countEvents(entityType, entityId))));
    }
}
