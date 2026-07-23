package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.EventRegistration;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.EventRegistrationService;
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
@RequestMapping("/api/event-registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;

    @PreAuthorize("hasAuthority('ACTIVITY_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<EventRegistration>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EventRegistration> paged = eventRegistrationService.findAll(pageable);
        PagedResponse<EventRegistration> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ACTIVITY_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventRegistration>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(eventRegistrationService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ACTIVITY_VIEW')")
    @PostMapping
    public ResponseEntity<ApiResponse<EventRegistration>> save(@Valid @RequestBody EventRegistration eventRegistration) {
        return ResponseEntity.ok(ApiResponse.success(eventRegistrationService.save(eventRegistration)));
    }

    @PreAuthorize("hasAuthority('ACTIVITY_VIEW')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventRegistration>> update(@PathVariable Long id, @Valid @RequestBody EventRegistration eventRegistration) {
        return ResponseEntity.ok(ApiResponse.success(eventRegistrationService.update(id, eventRegistration)));
    }

    @PreAuthorize("hasAuthority('ACTIVITY_VIEW')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        eventRegistrationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
