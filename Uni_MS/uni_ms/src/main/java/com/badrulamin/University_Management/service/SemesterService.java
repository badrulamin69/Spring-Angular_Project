package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Semester;
import com.badrulamin.University_Management.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SemesterService {

    private final SemesterRepository semesterRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Semester> findAll(Pageable pageable) {
        return semesterRepository.findAll(pageable);
    }

    public Semester findById(Long id) {
        return semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", id));
    }

    @Transactional
    public Semester save(Semester semester) {
        return semesterRepository.save(semester);
    }

    @Transactional
    public Semester update(Long id, Semester incoming) {
        Semester existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return semesterRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        semesterRepository.deleteById(id);
    }
}