package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Account;
import com.badrulamin.University_Management.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
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
