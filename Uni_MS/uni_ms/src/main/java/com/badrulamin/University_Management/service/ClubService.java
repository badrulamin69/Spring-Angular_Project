package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Club;
import com.badrulamin.University_Management.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;

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

    public Club update(Long id, Club club) {
        findById(id);
        club.setId(id);
        return clubRepository.save(club);
    }

    public void delete(Long id) {
        findById(id);
        clubRepository.deleteById(id);
    }
}
