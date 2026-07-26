package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Program;
import com.badrulamin.University_Management.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramService {

    private final ProgramRepository programRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Program> findAll(Pageable pageable) {
        return programRepository.findAll(pageable);
    }

    public Program findById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program", "id", id));
    }

    @Transactional
    public Program save(Program program) {
        return programRepository.save(program);
    }

    @Transactional
    public Program update(Long id, Program incoming) {
        Program existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return programRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        programRepository.deleteById(id);
    }
}