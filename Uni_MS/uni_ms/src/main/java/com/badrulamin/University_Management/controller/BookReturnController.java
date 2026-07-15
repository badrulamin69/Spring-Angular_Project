package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.BookReturn;
import com.badrulamin.University_Management.service.BookReturnService;
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
@RequestMapping("/api/book-returns")
@RequiredArgsConstructor
public class BookReturnController {

    private final BookReturnService bookReturnService;

    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    @GetMapping
    public ResponseEntity<PagedResponse<BookReturn>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BookReturn> paged = bookReturnService.findAll(pageable);
        PagedResponse<BookReturn> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    public ResponseEntity<BookReturn> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bookReturnService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    public ResponseEntity<BookReturn> save(@Valid @RequestBody BookReturn bookReturn) {
        return ResponseEntity.ok(bookReturnService.save(bookReturn));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    public ResponseEntity<BookReturn> update(@PathVariable Long id, @Valid @RequestBody BookReturn bookReturn) {
        return ResponseEntity.ok(bookReturnService.update(id, bookReturn));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookReturnService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
