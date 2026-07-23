package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.StudentDocument;
import com.badrulamin.University_Management.repository.StudentDocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class StudentDocumentService {

    private final StudentDocumentRepository studentDocumentRepository;

    public StudentDocumentService(StudentDocumentRepository studentDocumentRepository) {
        this.studentDocumentRepository = studentDocumentRepository;
    }

    public Page<StudentDocument> findAll(Pageable pageable) {
        return studentDocumentRepository.findAll(pageable);
    }

    public StudentDocument findById(Long id) {
        return studentDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudentDocument", "id", id));
    }

    public StudentDocument create(StudentDocument studentDocument) {
        return studentDocumentRepository.save(studentDocument);
    }

    public StudentDocument update(Long id, StudentDocument studentDocument) {
        findById(id);
        studentDocument.setId(id);
        return studentDocumentRepository.save(studentDocument);
    }

    public void delete(Long id) {
        findById(id);
        studentDocumentRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return studentDocumentRepository.countByStatus(status);
    }

    public long countByDocumentType(String documentType) {
        return studentDocumentRepository.countByDocumentType(documentType);
    }
}