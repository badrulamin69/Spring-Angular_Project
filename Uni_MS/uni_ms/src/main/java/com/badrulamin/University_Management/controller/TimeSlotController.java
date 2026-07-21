package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.payload.request.TimeSlotRequest;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.payload.response.TimeSlotResponse;
import com.badrulamin.University_Management.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TimeSlotResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TimeSlotResponse> paged = timeSlotService.findAll(pageable);
        PagedResponse<TimeSlotResponse> response = new PagedResponse<>(
                paged.getContent(), paged.getNumber(), paged.getSize(),
                paged.getTotalElements(), paged.getTotalPages(),
                paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TimeSlotResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(timeSlotService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<TimeSlotResponse>> create(@Valid @RequestBody TimeSlotRequest request) {
        return ResponseEntity.ok(ApiResponse.success("TimeSlot created successfully", timeSlotService.create(request)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TimeSlotResponse>> update(@PathVariable Long id, @Valid @RequestBody TimeSlotRequest request) {
        return ResponseEntity.ok(ApiResponse.success("TimeSlot updated successfully", timeSlotService.update(id, request)));
    }

    @PreAuthorize("hasAuthority('ROUTINE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        timeSlotService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("TimeSlot deleted successfully", null));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TimeSlotResponse>>> findActive() {
        return ResponseEntity.ok(ApiResponse.success(timeSlotService.findActive()));
    }

    @PreAuthorize("hasAuthority('ROUTINE_VIEW')")
    @GetMapping("/type/{slotType}")
    public ResponseEntity<ApiResponse<List<TimeSlotResponse>>> findBySlotType(@PathVariable String slotType) {
        return ResponseEntity.ok(ApiResponse.success(timeSlotService.findBySlotType(slotType)));
    }
}
