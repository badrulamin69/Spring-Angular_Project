package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Building;
import com.badrulamin.University_Management.entity.Classroom;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.request.ClassroomRequest;
import com.badrulamin.University_Management.payload.response.ClassroomResponse;
import com.badrulamin.University_Management.repository.BuildingRepository;
import com.badrulamin.University_Management.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final BuildingRepository buildingRepository;

    public Page<ClassroomResponse> findAll(Pageable pageable) {
        return classroomRepository.findAll(pageable).map(this::toResponse);
    }

    public ClassroomResponse findById(Long id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", id));
        return toResponse(classroom);
    }

    @Transactional
    public ClassroomResponse create(ClassroomRequest request) {
        if (classroomRepository.existsByBuilding_IdAndRoomNumber(request.getBuildingId(), request.getRoomNumber())) {
            throw new BusinessException("Room number already exists in this building");
        }

        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building", "id", request.getBuildingId()));

        Classroom classroom = new Classroom();
        classroom.setBuilding(building);
        classroom.setRoomNumber(request.getRoomNumber());
        classroom.setFloor(request.getFloor());
        classroom.setCapacity(request.getCapacity());
        classroom.setRoomType(request.getRoomType());
        classroom.setLab(request.isLab());
        classroom.setSmartClassroom(request.isSmartClassroom());
        classroom.setHasProjector(request.isHasProjector());
        classroom.setHasWhiteboard(request.isHasWhiteboard());
        classroom.setHasWifi(request.isHasWifi());
        classroom.setEquipment(request.getEquipment());
        classroom.setAvailable(request.isAvailable());
        classroom.setActive(request.isActive());
        classroom.setRemarks(request.getRemarks());

        return toResponse(classroomRepository.save(classroom));
    }

    @Transactional
    public ClassroomResponse update(Long id, ClassroomRequest request) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", id));

        boolean roomExists = classroomRepository.existsByBuilding_IdAndRoomNumber(request.getBuildingId(), request.getRoomNumber());
        if (roomExists && !(classroom.getBuilding().getId().equals(request.getBuildingId()) && classroom.getRoomNumber().equals(request.getRoomNumber()))) {
            throw new BusinessException("Room number already exists in this building");
        }

        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building", "id", request.getBuildingId()));

        classroom.setBuilding(building);
        classroom.setRoomNumber(request.getRoomNumber());
        classroom.setFloor(request.getFloor());
        classroom.setCapacity(request.getCapacity());
        classroom.setRoomType(request.getRoomType());
        classroom.setLab(request.isLab());
        classroom.setSmartClassroom(request.isSmartClassroom());
        classroom.setHasProjector(request.isHasProjector());
        classroom.setHasWhiteboard(request.isHasWhiteboard());
        classroom.setHasWifi(request.isHasWifi());
        classroom.setEquipment(request.getEquipment());
        classroom.setAvailable(request.isAvailable());
        classroom.setActive(request.isActive());
        classroom.setRemarks(request.getRemarks());

        return toResponse(classroomRepository.save(classroom));
    }

    @Transactional
    public void delete(Long id) {
        if (!classroomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Classroom", "id", id);
        }
        classroomRepository.deleteById(id);
    }

    public List<ClassroomResponse> search(String search, Long buildingId, String roomType, Boolean isLab, Boolean isSmartClassroom, Boolean isActive) {
        return classroomRepository.search(search, buildingId, roomType, isLab, isSmartClassroom, isActive)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ClassroomResponse> findByBuilding(Long buildingId) {
        return classroomRepository.findByBuilding_Id(buildingId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ClassroomResponse> findAvailable() {
        return classroomRepository.findByIsAvailableTrueAndIsActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ClassroomResponse toResponse(Classroom classroom) {
        ClassroomResponse response = new ClassroomResponse();
        response.setId(classroom.getId());
        response.setBuildingId(classroom.getBuilding() != null ? classroom.getBuilding().getId() : null);
        response.setBuildingName(classroom.getBuilding() != null ? classroom.getBuilding().getName() : null);
        response.setBuildingCode(classroom.getBuilding() != null ? classroom.getBuilding().getCode() : null);
        response.setRoomNumber(classroom.getRoomNumber());
        response.setFloor(classroom.getFloor());
        response.setCapacity(classroom.getCapacity());
        response.setRoomType(classroom.getRoomType());
        response.setLab(classroom.isLab());
        response.setSmartClassroom(classroom.isSmartClassroom());
        response.setHasProjector(classroom.isHasProjector());
        response.setHasWhiteboard(classroom.isHasWhiteboard());
        response.setHasWifi(classroom.isHasWifi());
        response.setEquipment(classroom.getEquipment());
        response.setAvailable(classroom.isAvailable());
        response.setActive(classroom.isActive());
        response.setRemarks(classroom.getRemarks());
        response.setCreatedAt(classroom.getCreatedAt());
        response.setUpdatedAt(classroom.getUpdatedAt());
        return response;
    }
}
