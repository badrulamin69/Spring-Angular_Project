package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Announcement;
import com.badrulamin.University_Management.service.AnnouncementService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PreAuthorize("hasAuthority('COMMUNICATION_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<Announcement>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Announcement> paged = announcementService.findAll(pageable);
        PagedResponse<Announcement> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMMUNICATION_VIEW')")
    public ResponseEntity<Announcement> findById(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('NOTICE_MANAGE')")
    public ResponseEntity<Announcement> save(@Valid @RequestBody Announcement announcement) {
        return ResponseEntity.ok(announcementService.save(announcement));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('NOTICE_MANAGE')")
    public ResponseEntity<Announcement> update(@PathVariable Long id, @Valid @RequestBody Announcement announcement) {
        return ResponseEntity.ok(announcementService.update(id, announcement));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('NOTICE_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
