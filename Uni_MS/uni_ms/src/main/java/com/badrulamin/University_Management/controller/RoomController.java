package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Room;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.PagedResponse;
import com.badrulamin.University_Management.service.RoomService;
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
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    @PreAuthorize("hasAuthority('HOSTEL_VIEW')")
    public ResponseEntity<ApiResponse<PagedResponse<Room>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Room> paged = roomService.findAll(pageable);
        PagedResponse<Room> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('HOSTEL_VIEW')")
    public ResponseEntity<ApiResponse<Room>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('HOSTEL_MANAGE')")
    public ResponseEntity<ApiResponse<Room>> save(@Valid @RequestBody Room room) {
        return ResponseEntity.ok(ApiResponse.success(roomService.save(room)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('HOSTEL_MANAGE')")
    public ResponseEntity<ApiResponse<Room>> update(@PathVariable Long id, @Valid @RequestBody Room room) {
        return ResponseEntity.ok(ApiResponse.success(roomService.update(id, room)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('HOSTEL_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
