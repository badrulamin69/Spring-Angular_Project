package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.BookIssue;
import com.badrulamin.University_Management.payload.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<PagedResponse<BookIssue>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BookIssue> paged = bookIssueService.findAll(pageable);
        PagedResponse<BookIssue> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    public ResponseEntity<ApiResponse<BookIssue>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bookIssueService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BOOK_ISSUE')")
    public ResponseEntity<ApiResponse<BookIssue>> save(@Valid @RequestBody BookIssue bookIssue) {
        return ResponseEntity.ok(ApiResponse.success(bookIssueService.save(bookIssue)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOK_ISSUE')")
    public ResponseEntity<ApiResponse<BookIssue>> update(@PathVariable Long id, @Valid @RequestBody BookIssue bookIssue) {
        return ResponseEntity.ok(ApiResponse.success(bookIssueService.update(id, bookIssue)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOK_ISSUE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bookIssueService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
