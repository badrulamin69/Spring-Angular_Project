package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.FeeStructure;
import com.badrulamin.University_Management.entity.Invoice;
import com.badrulamin.University_Management.entity.InvoiceItem;
import com.badrulamin.University_Management.entity.Semester;
import com.badrulamin.University_Management.entity.Student;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.FeeStructureRepository;
import com.badrulamin.University_Management.repository.InvoiceItemRepository;
import com.badrulamin.University_Management.repository.InvoiceRepository;
import com.badrulamin.University_Management.repository.SemesterRepository;
import com.badrulamin.University_Management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final StudentRepository studentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final SemesterRepository semesterRepository;

    public Page<Invoice> findAll(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    public Page<Invoice> search(String search, String status, Pageable pageable) {
        return invoiceRepository.search(search, status, pageable);
    }

    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
    }

    public Invoice findByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "invoiceNumber", invoiceNumber));
    }

    public Page<Invoice> findByStudentId(Long studentId, Pageable pageable) {
        return invoiceRepository.findByStudent_Id(studentId, pageable);
    }

    public Invoice generateInvoice(Long studentId, Long semesterId, String academicYear) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", semesterId));

        List<FeeStructure> feeStructures = feeStructureRepository
                .findBySemester_IdAndProgram_IdAndIsActiveTrue(semesterId, null);

        if (feeStructures.isEmpty()) {
            throw new BusinessException("No active fee structures found for the given semester");
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setStudent(student);
        invoice.setSemester(semester);
        invoice.setAcademicYear(academicYear);
        invoice.setStatus("PENDING");
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setTotalAmount(0.0);
        invoice.setPaidAmount(0.0);
        invoice.setDueAmount(0.0);
        invoice.setDiscountAmount(0.0);
        invoice.setFineAmount(0.0);

        double totalAmount = 0.0;

        for (FeeStructure feeStructure : feeStructures) {
            InvoiceItem item = new InvoiceItem();
            item.setFeeType(feeStructure.getFeeType());
            item.setDescription(feeStructure.getDescription() != null ? feeStructure.getDescription() : feeStructure.getFeeType().getName());
            item.setAmount(feeStructure.getAmount());
            item.setDiscountAmount(0.0);
            item.setNetAmount(feeStructure.getAmount());
            totalAmount += feeStructure.getAmount();

            Invoice savedInvoice = invoiceRepository.save(invoice);
            item.setInvoice(savedInvoice);
            invoiceItemRepository.save(item);

            invoice = savedInvoice;
        }

        invoice.setTotalAmount(totalAmount);
        invoice.setDueAmount(totalAmount);
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice updateStatus(Long id, String status) {
        Invoice invoice = findById(id);
        invoice.setStatus(status);
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public void delete(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Invoice", "id", id);
        }
        invoiceRepository.deleteById(id);
    }

    private String generateInvoiceNumber() {
        String prefix = "INV-" + Year.now().getValue() + "-";
        List<Invoice> allInvoices = invoiceRepository.findAll();

        AtomicLong maxSeq = new AtomicLong(0);
        allInvoices.forEach(inv -> {
            if (inv.getInvoiceNumber() != null && inv.getInvoiceNumber().startsWith(prefix)) {
                try {
                    String seqPart = inv.getInvoiceNumber().substring(prefix.length());
                    long seq = Long.parseLong(seqPart);
                    if (seq > maxSeq.get()) {
                        maxSeq.set(seq);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });

        long nextSeq = maxSeq.get() + 1;
        return prefix + String.format("%06d", nextSeq);
    }
}
