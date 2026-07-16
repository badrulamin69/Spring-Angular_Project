package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Program;
import com.badrulamin.University_Management.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;

    public Page<Program> findAll(Pageable pageable) {
        return programRepository.findAll(pageable);
    }

    public Program findById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program", "id", id));
    }

    public Program save(Program program) {
        return programRepository.save(program);
    }

    public Program update(Long id, Program program) {
        findById(id);
        program.setId(id);
        return programRepository.save(program);
    }

    public void delete(Long id) {
        findById(id);
        programRepository.deleteById(id);
    }
}
