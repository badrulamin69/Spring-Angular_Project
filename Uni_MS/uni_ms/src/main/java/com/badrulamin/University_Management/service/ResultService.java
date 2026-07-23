package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Result;
import com.badrulamin.University_Management.repository.ResultRepository;
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
public class ResultService {

    private final ResultRepository resultRepository;

    public Page<Result> findAll(Pageable pageable) {
        return resultRepository.findAll(pageable);
    }

    public Result findById(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result", "id", id));
    }

    public Result save(Result result) {
        return resultRepository.save(result);
    }

    public Result update(Long id, Result result) {
        findById(id);
        result.setId(id);
        return resultRepository.save(result);
    }

    public void delete(Long id) {
        findById(id);
        resultRepository.deleteById(id);
    }
}