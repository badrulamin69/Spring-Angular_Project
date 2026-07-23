package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Account;
import com.badrulamin.University_Management.repository.AccountRepository;
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
public class AccountService {

    private final AccountRepository accountRepository;

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account update(Long id, Account account) {
        findById(id);
        account.setId(id);
        return accountRepository.save(account);
    }

    public void delete(Long id) {
        findById(id);
        accountRepository.deleteById(id);
    }
}