package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.payload.request.BuildingRequest;
import com.badrulamin.University_Management.payload.response.BuildingResponse;
import com.badrulamin.University_Management.entity.Building;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public Page<BuildingResponse> findAll(Pageable pageable) {
        return buildingRepository.findAll(pageable).map(this::toResponse);
    }

    public BuildingResponse findById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building", "id", id));
        return toResponse(building);
    }

    public BuildingResponse create(BuildingRequest request) {
        if (buildingRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Building with code '" + request.getCode() + "' already exists");
        }
        if (buildingRepository.existsByName(request.getName())) {
            throw new BusinessException("Building with name '" + request.getName() + "' already exists");
        }

        Building building = new Building();
        building.setName(request.getName());
        building.setCode(request.getCode());
        building.setDescription(request.getDescription());
        building.setAddress(request.getAddress());
        building.setTotalFloors(request.getTotalFloors() != null ? request.getTotalFloors() : 1);
        building.setTotalRooms(request.getTotalRooms() != null ? request.getTotalRooms() : 0);
        building.setContactPerson(request.getContactPerson());
        building.setContactPhone(request.getContactPhone());
        building.setActive(request.isActive());

        return toResponse(buildingRepository.save(building));
    }

    public BuildingResponse update(Long id, BuildingRequest request) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building", "id", id));

        if (buildingRepository.existsByCode(request.getCode()) && !building.getCode().equals(request.getCode())) {
            throw new BusinessException("Building with code '" + request.getCode() + "' already exists");
        }
        if (buildingRepository.existsByName(request.getName()) && !building.getName().equals(request.getName())) {
            throw new BusinessException("Building with name '" + request.getName() + "' already exists");
        }

        building.setName(request.getName());
        building.setCode(request.getCode());
        building.setDescription(request.getDescription());
        building.setAddress(request.getAddress());
        building.setTotalFloors(request.getTotalFloors());
        building.setTotalRooms(request.getTotalRooms());
        building.setContactPerson(request.getContactPerson());
        building.setContactPhone(request.getContactPhone());
        building.setActive(request.isActive());

        return toResponse(buildingRepository.save(building));
    }

    public void delete(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Building", "id", id);
        }
        buildingRepository.deleteById(id);
    }

    public List<BuildingResponse> search(String search, Boolean isActive) {
        return buildingRepository.search(search, isActive).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<BuildingResponse> findActive() {
        return buildingRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BuildingResponse toResponse(Building building) {
        BuildingResponse response = new BuildingResponse();
        response.setId(building.getId());
        response.setName(building.getName());
        response.setCode(building.getCode());
        response.setDescription(building.getDescription());
        response.setAddress(building.getAddress());
        response.setTotalFloors(building.getTotalFloors());
        response.setTotalRooms(building.getTotalRooms());
        response.setContactPerson(building.getContactPerson());
        response.setContactPhone(building.getContactPhone());
        response.setActive(building.isActive());
        response.setCreatedAt(building.getCreatedAt());
        response.setUpdatedAt(building.getUpdatedAt());
        return response;
    }
}
