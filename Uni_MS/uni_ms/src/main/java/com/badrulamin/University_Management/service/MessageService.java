package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Message;
import com.badrulamin.University_Management.repository.MessageRepository;
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
public class MessageService {

    private final MessageRepository messageRepository;

    public Page<Message> findAll(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }

    public Message findById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));
    }

    @Transactional
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Transactional
    public Message update(Long id, Message message) {
        findById(id);
        message.setId(id);
        return messageRepository.save(message);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        messageRepository.deleteById(id);
    }
}