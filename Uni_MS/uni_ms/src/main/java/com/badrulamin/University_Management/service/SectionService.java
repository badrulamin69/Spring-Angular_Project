package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Section;
import com.badrulamin.University_Management.repository.SectionRepository;
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
public class SectionService {

    private final SectionRepository sectionRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Section> findAll(Pageable pageable) {
        return sectionRepository.findAll(pageable);
    }

    public Section findById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", id));
    }

    @Transactional
    public Section save(Section section) {
        return sectionRepository.save(section);
    }

    @Transactional
    public Section update(Long id, Section incoming) {
        Section existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return sectionRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        sectionRepository.deleteById(id);
    }
}