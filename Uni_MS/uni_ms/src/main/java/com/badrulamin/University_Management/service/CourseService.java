package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Course;
import com.badrulamin.University_Management.entity.Department;
import com.badrulamin.University_Management.entity.Program;
import com.badrulamin.University_Management.payload.response.CourseResponse;
import com.badrulamin.University_Management.repository.CourseRepository;
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
public class CourseService {

    private final CourseRepository courseRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    public Page<Course> searchCourses(String search, Long departmentId, Long programId, Pageable pageable) {
        return courseRepository.searchCourses(search, departmentId, programId, pageable);
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    @Transactional
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public Course update(Long id, Course incoming) {
        Course existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return courseRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        courseRepository.deleteById(id);
    }

    public CourseResponse toResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setName(course.getName());
        response.setCode(course.getCode());
        response.setDescription(course.getDescription());
        response.setDurationYears(course.getDurationYears());
        Department dept = course.getDepartment();
        response.setDepartmentId(dept != null ? dept.getId() : null);
        response.setDepartmentName(dept != null ? dept.getName() : null);
        Program prog = course.getProgram();
        response.setProgramId(prog != null ? prog.getId() : null);
        response.setProgramName(prog != null ? prog.getName() : null);
        response.setCreatedAt(course.getCreatedAt());
        response.setUpdatedAt(course.getUpdatedAt());
        return response;
    }
}