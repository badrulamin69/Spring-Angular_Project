package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.CourseRegistration;
import com.badrulamin.University_Management.repository.CourseRegistrationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CourseRegistrationService {

    private final CourseRegistrationRepository courseRegistrationRepository;

    public CourseRegistrationService(CourseRegistrationRepository courseRegistrationRepository) {
        this.courseRegistrationRepository = courseRegistrationRepository;
    }

    public Page<CourseRegistration> findAll(Pageable pageable) {
        return courseRegistrationRepository.findAll(pageable);
    }

    public CourseRegistration findById(Long id) {
        return courseRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseRegistration", "id", id));
    }

    public CourseRegistration create(CourseRegistration courseRegistration) {
        return courseRegistrationRepository.save(courseRegistration);
    }

    @Transactional
    public CourseRegistration update(Long id, CourseRegistration courseRegistration) {
        findById(id);
        courseRegistration.setId(id);
        return courseRegistrationRepository.save(courseRegistration);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        courseRegistrationRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return courseRegistrationRepository.countByStatus(status);
    }

    public long countByStudentIdAndSemesterId(Long studentId, Long semesterId) {
        return courseRegistrationRepository.countByStudent_IdAndSemester_Id(studentId, semesterId);
    }
}