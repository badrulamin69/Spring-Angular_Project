package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.StudentPromotion;
import com.badrulamin.University_Management.repository.StudentPromotionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class StudentPromotionService {

    private final StudentPromotionRepository studentPromotionRepository;

    public StudentPromotionService(StudentPromotionRepository studentPromotionRepository) {
        this.studentPromotionRepository = studentPromotionRepository;
    }

    public Page<StudentPromotion> findAll(Pageable pageable) {
        return studentPromotionRepository.findAll(pageable);
    }

    public StudentPromotion findById(Long id) {
        return studentPromotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudentPromotion", "id", id));
    }

    public StudentPromotion create(StudentPromotion studentPromotion) {
        return studentPromotionRepository.save(studentPromotion);
    }

    public StudentPromotion update(Long id, StudentPromotion studentPromotion) {
        findById(id);
        studentPromotion.setId(id);
        return studentPromotionRepository.save(studentPromotion);
    }

    public void delete(Long id) {
        findById(id);
        studentPromotionRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return studentPromotionRepository.countByStatus(status);
    }
}