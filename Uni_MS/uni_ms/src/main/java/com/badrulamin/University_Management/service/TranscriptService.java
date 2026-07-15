package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Transcript;
import com.badrulamin.University_Management.repository.TranscriptRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TranscriptService {

    private final TranscriptRepository transcriptRepository;

    public TranscriptService(TranscriptRepository transcriptRepository) {
        this.transcriptRepository = transcriptRepository;
    }

    public Page<Transcript> findAll(Pageable pageable) {
        return transcriptRepository.findAll(pageable);
    }

    public Transcript findById(Long id) {
        return transcriptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transcript not found with id: " + id));
    }

    public Transcript create(Transcript transcript) {
        return transcriptRepository.save(transcript);
    }

    public Transcript update(Long id, Transcript transcript) {
        findById(id);
        transcript.setId(id);
        return transcriptRepository.save(transcript);
    }

    public void delete(Long id) {
        findById(id);
        transcriptRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return transcriptRepository.countByStatus(status);
    }
}
