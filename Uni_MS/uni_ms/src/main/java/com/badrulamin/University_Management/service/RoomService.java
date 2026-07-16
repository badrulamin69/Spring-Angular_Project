package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Room;
import com.badrulamin.University_Management.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public Page<Room> findAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
    }

    public Room save(Room room) {
        return roomRepository.save(room);
    }

    public Room update(Long id, Room room) {
        findById(id);
        room.setId(id);
        return roomRepository.save(room);
    }

    public void delete(Long id) {
        findById(id);
        roomRepository.deleteById(id);
    }
}
