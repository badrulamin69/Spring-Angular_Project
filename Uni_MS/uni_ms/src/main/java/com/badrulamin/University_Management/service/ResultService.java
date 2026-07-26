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

    @Transactional
    public Result save(Result result) {
        return resultRepository.save(result);
    }

    @Transactional
    public Result update(Long id, Result incoming) {
        Result existing = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result", "id", id));
        if (incoming.getExam() != null) existing.setExam(incoming.getExam());
        if (incoming.getStudent() != null) existing.setStudent(incoming.getStudent());
        if (incoming.getTotalMarksObtained() != null) existing.setTotalMarksObtained(incoming.getTotalMarksObtained());
        if (incoming.getTotalMarks() != null) existing.setTotalMarks(incoming.getTotalMarks());
        if (incoming.getPercentage() != null) existing.setPercentage(incoming.getPercentage());
        if (incoming.getGrade() != null) existing.setGrade(incoming.getGrade());
        if (incoming.getResultStatus() != null) existing.setResultStatus(incoming.getResultStatus());
        if (incoming.getRemarks() != null) existing.setRemarks(incoming.getRemarks());
        return resultRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        resultRepository.deleteById(id);
    }
}