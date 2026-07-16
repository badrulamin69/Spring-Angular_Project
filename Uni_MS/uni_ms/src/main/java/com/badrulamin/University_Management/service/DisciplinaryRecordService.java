package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.DisciplinaryRecord;
import com.badrulamin.University_Management.repository.DisciplinaryRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
public class DisciplinaryRecordService {

    private final DisciplinaryRecordRepository disciplinaryRecordRepository;

    public DisciplinaryRecordService(DisciplinaryRecordRepository disciplinaryRecordRepository) {
        this.disciplinaryRecordRepository = disciplinaryRecordRepository;
    }

    public Page<DisciplinaryRecord> findAll(Pageable pageable) {
        return disciplinaryRecordRepository.findAll(pageable);
    }

    public DisciplinaryRecord findById(Long id) {
        return disciplinaryRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DisciplinaryRecord", "id", id));
    }

    public DisciplinaryRecord create(DisciplinaryRecord disciplinaryRecord) {
        return disciplinaryRecordRepository.save(disciplinaryRecord);
    }

    public DisciplinaryRecord update(Long id, DisciplinaryRecord disciplinaryRecord) {
        findById(id);
        disciplinaryRecord.setId(id);
        return disciplinaryRecordRepository.save(disciplinaryRecord);
    }

    public void delete(Long id) {
        findById(id);
        disciplinaryRecordRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return disciplinaryRecordRepository.countByStatus(status);
    }

    public long countBySeverity(String severity) {
        return disciplinaryRecordRepository.countBySeverity(severity);
    }
}
