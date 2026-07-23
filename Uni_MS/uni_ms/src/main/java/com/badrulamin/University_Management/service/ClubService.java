package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Club;
import com.badrulamin.University_Management.repository.ClubRepository;
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
public class ClubService {

    private final ClubRepository clubRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Club> findAll(Pageable pageable) {
        return clubRepository.findAll(pageable);
    }

    public Club findById(Long id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club", "id", id));
    }

    public Club save(Club club) {
        return clubRepository.save(club);
    }

    @Transactional
    public Club update(Long id, Club incoming) {
        Club existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return clubRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        clubRepository.deleteById(id);
    }
}