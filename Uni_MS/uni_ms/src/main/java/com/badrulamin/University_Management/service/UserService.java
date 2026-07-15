package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User update(Long id, User user) {
        findById(id);
        user.setId(id);
        return userRepository.save(user);
    }

    public void delete(Long id) {
        findById(id);
        userRepository.deleteById(id);
    }
}
