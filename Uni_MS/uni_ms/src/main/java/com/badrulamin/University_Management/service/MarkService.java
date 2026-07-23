package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Mark;
import com.badrulamin.University_Management.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarkService {

    private final MarkRepository markRepository;

    public Page<Mark> findAll(Pageable pageable) {
        return markRepository.findAll(pageable);
    }

    public Mark findById(Long id) {
        return markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark", "id", id));
    }

    public Mark save(Mark mark) {
        return markRepository.save(mark);
    }

    public Mark update(Long id, Mark mark) {
        findById(id);
        mark.setId(id);
        return markRepository.save(mark);
    }

    public void delete(Long id) {
        findById(id);
        markRepository.deleteById(id);
    }
}