package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Notice;
import com.badrulamin.University_Management.repository.NoticeRepository;
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
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Notice> findAll(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    public Page<Notice> searchNotices(String search, String noticeType, String status, String priority, Pageable pageable) {
        return noticeRepository.searchNotices(search, noticeType, status, priority, pageable);
    }

    public Notice findById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", "id", id));
    }

    @Transactional
    public Notice save(Notice notice) {
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice update(Long id, Notice incoming) {
        Notice existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return noticeRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        noticeRepository.deleteById(id);
    }
}