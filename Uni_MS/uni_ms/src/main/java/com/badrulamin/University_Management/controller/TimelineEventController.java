package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.TimelineEvent;
import com.badrulamin.University_Management.service.TimelineEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/timeline")
@RequiredArgsConstructor
public class TimelineEventController {

    private final TimelineEventService timelineService;

    @GetMapping
    public ResponseEntity<Page<TimelineEvent>> getTimeline(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(
            timelineService.getTimeline(entityType, entityId, PageRequest.of(page, size)));
    }

    @GetMapping("/recent")
    public ResponseEntity<Page<TimelineEvent>> getRecentTimeline(
            @RequestParam String entityType,
            @RequestParam Long entityId) {
        return ResponseEntity.ok(
            timelineService.getRecentTimeline(entityType, entityId));
    }

    @GetMapping("/count")
    public ResponseEntity<java.util.Map<String, Long>> countEvents(
            @RequestParam String entityType,
            @RequestParam Long entityId) {
        return ResponseEntity.ok(
            java.util.Map.of("count", timelineService.countEvents(entityType, entityId)));
    }
}
