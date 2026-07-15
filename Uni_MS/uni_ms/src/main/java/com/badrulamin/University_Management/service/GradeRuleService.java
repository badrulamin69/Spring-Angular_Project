package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.GradeRule;
import com.badrulamin.University_Management.repository.GradeRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeRuleService {

    private final GradeRuleRepository gradeRuleRepository;

    public Page<GradeRule> findAll(Pageable pageable) {
        return gradeRuleRepository.findAll(pageable);
    }

    public GradeRule findById(Long id) {
        return gradeRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GradeRule not found with id: " + id));
    }

    public GradeRule save(GradeRule gradeRule) {
        return gradeRuleRepository.save(gradeRule);
    }

    public GradeRule update(Long id, GradeRule gradeRule) {
        findById(id);
        gradeRule.setId(id);
        return gradeRuleRepository.save(gradeRule);
    }

    public void delete(Long id) {
        findById(id);
        gradeRuleRepository.deleteById(id);
    }
}
