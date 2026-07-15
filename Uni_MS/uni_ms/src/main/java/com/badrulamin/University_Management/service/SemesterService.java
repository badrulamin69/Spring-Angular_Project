package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Semester;
import com.badrulamin.University_Management.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class SemesterService {

    private final SemesterRepository semesterRepository;

    public Page<Semester> findAll(Pageable pageable) {
        return semesterRepository.findAll(pageable);
    }

    public Semester findById(Long id) {
        return semesterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semester not found with id: " + id));
    }

    public Semester save(Semester semester) {
        return semesterRepository.save(semester);
    }

    public Semester update(Long id, Semester semester) {
        findById(id);
        semester.setId(id);
        return semesterRepository.save(semester);
    }

    public void delete(Long id) {
        findById(id);
        semesterRepository.deleteById(id);
    }
}
