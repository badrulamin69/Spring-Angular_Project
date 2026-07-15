package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.ClassRoutine;
import com.badrulamin.University_Management.repository.ClassRoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassRoutineService {

    private final ClassRoutineRepository classRoutineRepository;

    public Page<ClassRoutine> findAll(Pageable pageable) {
        return classRoutineRepository.findAll(pageable);
    }

    public ClassRoutine findById(Long id) {
        return classRoutineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClassRoutine not found with id: " + id));
    }

    public ClassRoutine save(ClassRoutine classRoutine) {
        return classRoutineRepository.save(classRoutine);
    }

    public ClassRoutine update(Long id, ClassRoutine classRoutine) {
        findById(id);
        classRoutine.setId(id);
        return classRoutineRepository.save(classRoutine);
    }

    public void delete(Long id) {
        findById(id);
        classRoutineRepository.deleteById(id);
    }
}
