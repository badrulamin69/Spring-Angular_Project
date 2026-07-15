package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.BookCategory;
import com.badrulamin.University_Management.service.BookCategoryService;
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
@RequestMapping("/api/book-categories")
@RequiredArgsConstructor
public class BookCategoryController {

    private final BookCategoryService bookCategoryService;

    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<BookCategory>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BookCategory> paged = bookCategoryService.findAll(pageable);
        PagedResponse<BookCategory> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    public ResponseEntity<BookCategory> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bookCategoryService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    public ResponseEntity<BookCategory> save(@Valid @RequestBody BookCategory bookCategory) {
        return ResponseEntity.ok(bookCategoryService.save(bookCategory));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    public ResponseEntity<BookCategory> update(@PathVariable Long id, @Valid @RequestBody BookCategory bookCategory) {
        return ResponseEntity.ok(bookCategoryService.update(id, bookCategory));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
