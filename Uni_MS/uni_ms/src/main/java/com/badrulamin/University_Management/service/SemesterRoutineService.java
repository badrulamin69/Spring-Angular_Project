package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.SemesterRoutine;
import com.badrulamin.University_Management.repository.SemesterRoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SemesterRoutineService {

    private final SemesterRoutineRepository semesterRoutineRepository;

    public Page<SemesterRoutine> findAll(Pageable pageable) {
        return semesterRoutineRepository.findAll(pageable);
    }

    public SemesterRoutine findById(Long id) {
        return semesterRoutineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SemesterRoutine", "id", id));
    }

    public SemesterRoutine save(SemesterRoutine semesterRoutine) {
        return semesterRoutineRepository.save(semesterRoutine);
    }

    public SemesterRoutine update(Long id, SemesterRoutine semesterRoutine) {
        findById(id);
        semesterRoutine.setId(id);
        return semesterRoutineRepository.save(semesterRoutine);
    }

    public void delete(Long id) {
        findById(id);
        semesterRoutineRepository.deleteById(id);
    }
}