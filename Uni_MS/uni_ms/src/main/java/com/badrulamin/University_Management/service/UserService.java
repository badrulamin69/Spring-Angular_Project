package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public User save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User update(Long id, User user) {
        User existing = findById(id);
        if (user.getFirstName() != null) existing.setFirstName(user.getFirstName());
        if (user.getLastName() != null) existing.setLastName(user.getLastName());
        if (user.getEmail() != null) existing.setEmail(user.getEmail());
        if (user.getPhone() != null) existing.setPhone(user.getPhone());
        if (user.getUsername() != null) existing.setUsername(user.getUsername());
        if (user.getRole() != null) existing.setRole(user.getRole());
        if (user.getActive() != null) existing.setActive(user.getActive());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        userRepository.deleteById(id);
    }
}