package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Club;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.ClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.badrulamin.University_Management.payload.response.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PreAuthorize("hasAuthority('ACTIVITY_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Club>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Club> paged = clubService.findAll(pageable);
        PagedResponse<Club> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACTIVITY_VIEW')")
    public ResponseEntity<ApiResponse<Club>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(clubService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ACTIVITY_MANAGE')")
    public ResponseEntity<ApiResponse<Club>> save(@Valid @RequestBody Club club) {
        return ResponseEntity.ok(ApiResponse.success(clubService.save(club)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ACTIVITY_MANAGE')")
    public ResponseEntity<ApiResponse<Club>> update(@PathVariable Long id, @Valid @RequestBody Club club) {
        return ResponseEntity.ok(ApiResponse.success(clubService.update(id, club)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ACTIVITY_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        clubService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
