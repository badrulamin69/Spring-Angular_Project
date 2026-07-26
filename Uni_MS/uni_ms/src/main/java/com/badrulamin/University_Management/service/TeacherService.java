package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Teacher;
import com.badrulamin.University_Management.repository.TeacherRepository;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Teacher> findAll(Pageable pageable) {
        return teacherRepository.findAll(pageable);
    }

    public Page<Teacher> searchTeachers(String search, Long departmentId, Long facultyId, String designation, String status, Pageable pageable) {
        return teacherRepository.searchTeachers(search, departmentId, facultyId, designation, status, pageable);
    }

    public Teacher findById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));
    }

    @Transactional
    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher update(Long id, Teacher incoming) {
        Teacher existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return teacherRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        teacherRepository.deleteById(id);
    }
}