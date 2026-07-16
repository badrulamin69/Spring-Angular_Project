package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Notice;
import com.badrulamin.University_Management.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

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

    public Notice save(Notice notice) {
        return noticeRepository.save(notice);
    }

    public Notice update(Long id, Notice notice) {
        findById(id);
        notice.setId(id);
        return noticeRepository.save(notice);
    }

    public void delete(Long id) {
        findById(id);
        noticeRepository.deleteById(id);
    }
}
