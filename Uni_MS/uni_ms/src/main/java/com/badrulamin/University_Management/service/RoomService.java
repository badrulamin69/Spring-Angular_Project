package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Room;
import com.badrulamin.University_Management.repository.RoomRepository;
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
public class RoomService {

    private final RoomRepository roomRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Room> findAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
    }

    @Transactional
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Transactional
    public Room update(Long id, Room incoming) {
        Room existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return roomRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        roomRepository.deleteById(id);
    }
}