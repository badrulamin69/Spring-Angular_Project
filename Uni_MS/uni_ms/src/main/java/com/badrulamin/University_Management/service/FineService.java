package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.FeeType;
import com.badrulamin.University_Management.entity.Fine;
import com.badrulamin.University_Management.entity.Invoice;
import com.badrulamin.University_Management.entity.Student;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.FeeTypeRepository;
import com.badrulamin.University_Management.repository.FineRepository;
import com.badrulamin.University_Management.repository.InvoiceRepository;
import com.badrulamin.University_Management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FineService {

    private final FineRepository fineRepository;
    private final StudentRepository studentRepository;
    private final InvoiceRepository invoiceRepository;
    private final FeeTypeRepository feeTypeRepository;

    public Page<Fine> findAll(Pageable pageable) {
        return fineRepository.findAll(pageable);
    }

    public Fine findById(Long id) {
        return fineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fine", "id", id));
    }

    public List<Fine> findByStudentId(Long studentId) {
        return fineRepository.findByStudent_Id(studentId);
    }

    public List<Fine> findByStatus(String status) {
        return fineRepository.findByStatus(status);
    }

    @Transactional
    public Fine save(Fine fine) {
        return fineRepository.save(fine);
    }

    @Transactional
    public Fine update(Long id, Fine fine) {
        Fine existing = findById(id);
        existing.setStudent(fine.getStudent());
        existing.setInvoice(fine.getInvoice());
        existing.setFeeType(fine.getFeeType());
        existing.setAmount(fine.getAmount());
        existing.setReason(fine.getReason());
        existing.setIssuedBy(fine.getIssuedBy());
        existing.setStatus(fine.getStatus());
        existing.setIssuedDate(fine.getIssuedDate());
        return fineRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!fineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fine", "id", id);
        }
        fineRepository.deleteById(id);
    }

    @Transactional
    public Fine issueFine(Long studentId, Long invoiceId, Long feeTypeId, Double amount, String reason, String issuedBy) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        FeeType feeType = feeTypeRepository.findById(feeTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeType", "id", feeTypeId));

        Fine fine = new Fine();
        fine.setStudent(student);
        fine.setInvoice(invoice);
        fine.setFeeType(feeType);
        fine.setAmount(amount);
        fine.setReason(reason);
        fine.setIssuedBy(issuedBy);
        fine.setStatus("PENDING");
        fine.setIssuedDate(LocalDate.now());

        invoice.setFineAmount(invoice.getFineAmount() + amount);
        invoice.setDueAmount(invoice.getDueAmount() + amount);
        invoiceRepository.save(invoice);

        return fineRepository.save(fine);
    }

    @Transactional
    public Fine waiveFine(Long fineId, String waivedBy) {
        Fine fine = findById(fineId);
        fine.setStatus("WAIVED");
        fine.setIssuedBy(waivedBy);
        return fineRepository.save(fine);
    }
}