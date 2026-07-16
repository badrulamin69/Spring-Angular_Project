package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.BookIssue;
import com.badrulamin.University_Management.repository.BookIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class BookIssueService {

    private final BookIssueRepository bookIssueRepository;

    public Page<BookIssue> findAll(Pageable pageable) {
        return bookIssueRepository.findAll(pageable);
    }

    public BookIssue findById(Long id) {
        return bookIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BookIssue", "id", id));
    }

    public BookIssue save(BookIssue bookIssue) {
        return bookIssueRepository.save(bookIssue);
    }

    public BookIssue update(Long id, BookIssue bookIssue) {
        findById(id);
        bookIssue.setId(id);
        return bookIssueRepository.save(bookIssue);
    }

    public void delete(Long id) {
        findById(id);
        bookIssueRepository.deleteById(id);
    }
}
