package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Building;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.request.BuildingRequest;
import com.badrulamin.University_Management.payload.response.BuildingResponse;
import com.badrulamin.University_Management.repository.BuildingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuildingServiceTest {

    @Mock
    private BuildingRepository buildingRepository;

    @InjectMocks
    private BuildingService buildingService;

    @Test
    void create_validRequest_returnsBuildingResponse() {
        BuildingRequest request = new BuildingRequest();
        request.setName("Science Block");
        request.setCode("SB01");
        request.setTotalFloors(5);
        request.setTotalRooms(50);
        request.setActive(true);

        when(buildingRepository.existsByCode("SB01")).thenReturn(false);
        when(buildingRepository.existsByName("Science Block")).thenReturn(false);

        Building saved = new Building();
        saved.setId(1L);
        saved.setName("Science Block");
        saved.setCode("SB01");
        saved.setTotalFloors(5);
        saved.setTotalRooms(50);
        saved.setActive(true);
        when(buildingRepository.save(any(Building.class))).thenReturn(saved);

        BuildingResponse response = buildingService.create(request);

        assertEquals("Science Block", response.getName());
        assertEquals("SB01", response.getCode());
        assertEquals(5, response.getTotalFloors());
        verify(buildingRepository).save(any(Building.class));
    }

    @Test
    void create_duplicateCode_throwsBusinessException() {
        BuildingRequest request = new BuildingRequest();
        request.setName("New Building");
        request.setCode("SB01");

        when(buildingRepository.existsByCode("SB01")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> buildingService.create(request));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(buildingRepository, never()).save(any());
    }

    @Test
    void create_duplicateName_throwsBusinessException() {
        BuildingRequest request = new BuildingRequest();
        request.setName("Existing Name");
        request.setCode("NB01");

        when(buildingRepository.existsByCode("NB01")).thenReturn(false);
        when(buildingRepository.existsByName("Existing Name")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> buildingService.create(request));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(buildingRepository, never()).save(any());
    }

    @Test
    void findById_existingBuilding_returnsResponse() {
        Building building = new Building();
        building.setId(1L);
        building.setName("Admin Block");
        building.setCode("AB01");
        building.setActive(true);

        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));

        BuildingResponse response = buildingService.findById(1L);

        assertEquals("Admin Block", response.getName());
        assertEquals("AB01", response.getCode());
        verify(buildingRepository).findById(1L);
    }

    @Test
    void findById_nonExisting_throwsException() {
        when(buildingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> buildingService.findById(999L));
    }

    @Test
    void delete_existingBuilding_deletesSuccessfully() {
        when(buildingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(buildingRepository).deleteById(1L);

        buildingService.delete(1L);

        verify(buildingRepository).existsById(1L);
        verify(buildingRepository).deleteById(1L);
    }

    @Test
    void delete_nonExisting_throwsException() {
        when(buildingRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> buildingService.delete(999L));

        verify(buildingRepository, never()).deleteById(anyLong());
    }

    @Test
    void findActive_returnsActiveBuildings() {
        Building b1 = new Building();
        b1.setId(1L);
        b1.setName("Block A");
        b1.setCode("BA01");
        b1.setActive(true);

        when(buildingRepository.findByIsActiveTrue()).thenReturn(List.of(b1));

        List<BuildingResponse> result = buildingService.findActive();

        assertEquals(1, result.size());
        assertEquals("Block A", result.get(0).getName());
        verify(buildingRepository).findByIsActiveTrue();
    }
}
