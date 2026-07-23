package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Exam;
import com.badrulamin.University_Management.entity.Course;
import com.badrulamin.University_Management.entity.Subject;
import com.badrulamin.University_Management.payload.response.ExamResponse;
import com.badrulamin.University_Management.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamService {

    private final ExamRepository examRepository;

    public Page<Exam> findAll(Pageable pageable) {
        return examRepository.findAll(pageable);
    }

    public Page<Exam> searchExams(String keyword, Long courseId, String examType, Pageable pageable) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCourse = courseId != null;
        boolean hasType = examType != null && !examType.trim().isEmpty();

        if (hasKeyword && (hasCourse || hasType)) {
            return examRepository.searchExamsWithFilters(keyword.trim(), courseId, hasType ? examType.trim() : null, pageable);
        } else if (hasKeyword) {
            return examRepository.searchExams(keyword.trim(), pageable);
        } else if (hasCourse || hasType) {
            return examRepository.findAllWithFilters(courseId, hasType ? examType.trim() : null, pageable);
        }
        return examRepository.findAll(pageable);
    }

    public Exam findById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));
    }

    public Exam save(Exam exam) {
        return examRepository.save(exam);
    }

    public Exam update(Long id, Exam exam) {
        findById(id);
        exam.setId(id);
        return examRepository.save(exam);
    }

    public void delete(Long id) {
        findById(id);
        examRepository.deleteById(id);
    }

    public ExamResponse toResponse(Exam exam) {
        ExamResponse response = new ExamResponse();
        response.setId(exam.getId());
        response.setName(exam.getName());
        response.setExamType(exam.getExamType());
        Course course = exam.getCourse();
        response.setCourseId(course != null ? course.getId() : null);
        response.setCourseName(course != null ? course.getName() : null);
        Subject subject = exam.getSubject();
        response.setSubjectId(subject != null ? subject.getId() : null);
        response.setSubjectName(subject != null ? subject.getName() : null);
        response.setTotalMarks(exam.getTotalMarks());
        response.setPassingMarks(exam.getPassingMarks());
        response.setExamDate(exam.getExamDate());
        response.setDurationMinutes(exam.getDurationMinutes());
        response.setDescription(exam.getDescription());
        response.setCreatedAt(exam.getCreatedAt());
        response.setUpdatedAt(exam.getUpdatedAt());
        return response;
    }
}
