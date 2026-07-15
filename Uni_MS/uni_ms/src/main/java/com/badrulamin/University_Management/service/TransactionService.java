package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Transaction;
import com.badrulamin.University_Management.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Page<Transaction> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction update(Long id, Transaction transaction) {
        findById(id);
        transaction.setId(id);
        return transactionRepository.save(transaction);
    }

    public void delete(Long id) {
        findById(id);
        transactionRepository.deleteById(id);
    }
}
