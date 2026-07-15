package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.BookIssue;
import com.badrulamin.University_Management.service.BookIssueService;
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
@RequestMapping("/api/book-issues")
@RequiredArgsConstructor
public class BookIssueController {

    private final BookIssueService bookIssueService;

    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<BookIssue>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BookIssue> paged = bookIssueService.findAll(pageable);
        PagedResponse<BookIssue> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    public ResponseEntity<BookIssue> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bookIssueService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BOOK_ISSUE')")
    public ResponseEntity<BookIssue> save(@Valid @RequestBody BookIssue bookIssue) {
        return ResponseEntity.ok(bookIssueService.save(bookIssue));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOK_ISSUE')")
    public ResponseEntity<BookIssue> update(@PathVariable Long id, @Valid @RequestBody BookIssue bookIssue) {
        return ResponseEntity.ok(bookIssueService.update(id, bookIssue));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOK_ISSUE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookIssueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
