package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.BookReturn;
import com.badrulamin.University_Management.repository.BookReturnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookReturnService {

    private final BookReturnRepository bookReturnRepository;

    public Page<BookReturn> findAll(Pageable pageable) {
        return bookReturnRepository.findAll(pageable);
    }

    public BookReturn findById(Long id) {
        return bookReturnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookReturn not found with id: " + id));
    }

    public BookReturn save(BookReturn bookReturn) {
        return bookReturnRepository.save(bookReturn);
    }

    public BookReturn update(Long id, BookReturn bookReturn) {
        findById(id);
        bookReturn.setId(id);
        return bookReturnRepository.save(bookReturn);
    }

    public void delete(Long id) {
        findById(id);
        bookReturnRepository.deleteById(id);
    }
}
