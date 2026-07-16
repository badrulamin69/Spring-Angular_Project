package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Invoice;
import com.badrulamin.University_Management.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Page<Invoice> findAll(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    public Page<Invoice> searchInvoices(String keyword, String status, Long studentId, Pageable pageable) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();
        boolean hasStudent = studentId != null;

        if (hasKeyword && (hasStatus || hasStudent)) {
            return invoiceRepository.searchInvoicesWithFilters(keyword.trim(), hasStatus ? status.trim() : null, hasStudent ? studentId : null, pageable);
        } else if (hasKeyword) {
            return invoiceRepository.searchInvoices(keyword.trim(), pageable);
        } else if (hasStatus || hasStudent) {
            return invoiceRepository.findAllWithFilters(hasStatus ? status.trim() : null, hasStudent ? studentId : null, pageable);
        }
        return invoiceRepository.findAll(pageable);
    }

    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
    }

    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Invoice update(Long id, Invoice invoice) {
        findById(id);
        invoice.setId(id);
        return invoiceRepository.save(invoice);
    }

    public void delete(Long id) {
        findById(id);
        invoiceRepository.deleteById(id);
    }
}
