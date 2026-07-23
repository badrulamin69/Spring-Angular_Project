package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Book;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.BookService;
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
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Book>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean available) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> paged = bookService.searchBooks(search, categoryId, available, pageable);
        PagedResponse<Book> response = new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LIBRARY_VIEW')")
    public ResponseEntity<ApiResponse<Book>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bookService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    public ResponseEntity<ApiResponse<Book>> save(@Valid @RequestBody Book book) {
        return ResponseEntity.ok(ApiResponse.success(bookService.save(book)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    public ResponseEntity<ApiResponse<Book>> update(@PathVariable Long id, @Valid @RequestBody Book book) {
        return ResponseEntity.ok(ApiResponse.success(bookService.update(id, book)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
