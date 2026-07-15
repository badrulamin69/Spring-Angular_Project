package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.BookCategory;
import com.badrulamin.University_Management.repository.BookCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCategoryService {

    private final BookCategoryRepository bookCategoryRepository;

    public Page<BookCategory> findAll(Pageable pageable) {
        return bookCategoryRepository.findAll(pageable);
    }

    public BookCategory findById(Long id) {
        return bookCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookCategory not found with id: " + id));
    }

    public BookCategory save(BookCategory bookCategory) {
        return bookCategoryRepository.save(bookCategory);
    }

    public BookCategory update(Long id, BookCategory bookCategory) {
        findById(id);
        bookCategory.setId(id);
        return bookCategoryRepository.save(bookCategory);
    }

    public void delete(Long id) {
        findById(id);
        bookCategoryRepository.deleteById(id);
    }
}
