package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Section;
import com.badrulamin.University_Management.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    public Page<Section> findAll(Pageable pageable) {
        return sectionRepository.findAll(pageable);
    }

    public Section findById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", id));
    }

    public Section save(Section section) {
        return sectionRepository.save(section);
    }

    public Section update(Long id, Section section) {
        findById(id);
        section.setId(id);
        return sectionRepository.save(section);
    }

    public void delete(Long id) {
        findById(id);
        sectionRepository.deleteById(id);
    }
}
