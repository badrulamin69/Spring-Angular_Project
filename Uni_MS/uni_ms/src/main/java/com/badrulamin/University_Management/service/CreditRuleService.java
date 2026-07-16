package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.CreditRule;
import com.badrulamin.University_Management.repository.CreditRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class CreditRuleService {

    private final CreditRuleRepository creditRuleRepository;

    public Page<CreditRule> findAll(Pageable pageable) {
        return creditRuleRepository.findAll(pageable);
    }

    public CreditRule findById(Long id) {
        return creditRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CreditRule", "id", id));
    }

    public CreditRule save(CreditRule creditRule) {
        return creditRuleRepository.save(creditRule);
    }

    public CreditRule update(Long id, CreditRule creditRule) {
        findById(id);
        creditRule.setId(id);
        return creditRuleRepository.save(creditRule);
    }

    public void delete(Long id) {
        findById(id);
        creditRuleRepository.deleteById(id);
    }
}
