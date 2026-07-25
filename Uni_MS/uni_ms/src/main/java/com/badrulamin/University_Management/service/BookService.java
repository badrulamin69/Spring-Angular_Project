package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Book;
import com.badrulamin.University_Management.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Page<Book> searchBooks(String keyword, Long categoryId, Boolean available, Pageable pageable) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = categoryId != null;
        boolean hasAvailable = available != null;

        if (hasKeyword && (hasCategory || hasAvailable)) {
            return bookRepository.searchBooksWithFilters(keyword.trim(), hasCategory ? categoryId : null, hasAvailable ? available : null, pageable);
        } else if (hasKeyword) {
            return bookRepository.searchBooks(keyword.trim(), pageable);
        } else if (hasCategory || hasAvailable) {
            return bookRepository.findAllWithFilters(hasCategory ? categoryId : null, hasAvailable ? available : null, pageable);
        }
        return bookRepository.findAll(pageable);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
    }

    @Transactional
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, Book incoming) {
        Book existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return bookRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        bookRepository.deleteById(id);
    }
}