package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.LeaveRequest;
import com.badrulamin.University_Management.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;

    public Page<LeaveRequest> findAll(Pageable pageable) {
        return leaveRequestRepository.findAll(pageable);
    }

    public LeaveRequest findById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", id));
    }

    public LeaveRequest save(LeaveRequest leaveRequest) {
        return leaveRequestRepository.save(leaveRequest);
    }

    public LeaveRequest update(Long id, LeaveRequest leaveRequest) {
        findById(id);
        leaveRequest.setId(id);
        return leaveRequestRepository.save(leaveRequest);
    }

    public void delete(Long id) {
        findById(id);
        leaveRequestRepository.deleteById(id);
    }
}
