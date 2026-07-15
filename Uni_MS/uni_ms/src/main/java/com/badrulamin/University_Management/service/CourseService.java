package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Course;
import com.badrulamin.University_Management.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    public Page<Course> searchCourses(String search, Long departmentId, Long programId, Pageable pageable) {
        return courseRepository.searchCourses(search, departmentId, programId, pageable);
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public Course update(Long id, Course course) {
        findById(id);
        course.setId(id);
        return courseRepository.save(course);
    }

    public void delete(Long id) {
        findById(id);
        courseRepository.deleteById(id);
    }
}
