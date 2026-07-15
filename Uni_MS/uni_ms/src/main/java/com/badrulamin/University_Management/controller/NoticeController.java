package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Notice;
import com.badrulamin.University_Management.service.NoticeService;
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
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PreAuthorize("hasAuthority('COMMUNICATION_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<Notice>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String noticeType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Notice> paged = noticeService.searchNotices(search, noticeType, status, priority, pageable);
        PagedResponse<Notice> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('COMMUNICATION_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<Notice> findById(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.findById(id));
    }

    @PreAuthorize("hasAuthority('NOTICE_MANAGE')")
    @PostMapping
    public ResponseEntity<Notice> save(@Valid @RequestBody Notice notice) {
        return ResponseEntity.ok(noticeService.save(notice));
    }

    @PreAuthorize("hasAuthority('NOTICE_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<Notice> update(@PathVariable Long id, @Valid @RequestBody Notice notice) {
        return ResponseEntity.ok(noticeService.update(id, notice));
    }

    @PreAuthorize("hasAuthority('NOTICE_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
