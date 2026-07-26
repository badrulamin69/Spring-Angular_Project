package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Transaction;
import com.badrulamin.University_Management.repository.TransactionRepository;
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
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Page<Transaction> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
    }

    @Transactional
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction update(Long id, Transaction incoming) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        if (incoming.getAccount() != null) existing.setAccount(incoming.getAccount());
        if (incoming.getTransactionType() != null) existing.setTransactionType(incoming.getTransactionType());
        if (incoming.getAmount() != null) existing.setAmount(incoming.getAmount());
        if (incoming.getDescription() != null) existing.setDescription(incoming.getDescription());
        if (incoming.getReferenceType() != null) existing.setReferenceType(incoming.getReferenceType());
        if (incoming.getReferenceId() != null) existing.setReferenceId(incoming.getReferenceId());
        if (incoming.getTransactionDate() != null) existing.setTransactionDate(incoming.getTransactionDate());
        return transactionRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        transactionRepository.deleteById(id);
    }
}