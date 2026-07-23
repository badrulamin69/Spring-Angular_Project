package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Event;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PreAuthorize("hasAuthority('ACTIVITY_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Event>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Event> paged = eventService.findAll(pageable);
        PagedResponse<Event> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ACTIVITY_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(eventService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ACTIVITY_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<Event>> save(@Valid @RequestBody Event event) {
        return ResponseEntity.ok(ApiResponse.success(eventService.save(event)));
    }

    @PreAuthorize("hasAuthority('ACTIVITY_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> update(@PathVariable Long id, @Valid @RequestBody Event event) {
        return ResponseEntity.ok(ApiResponse.success(eventService.update(id, event)));
    }

    @PreAuthorize("hasAuthority('ACTIVITY_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
