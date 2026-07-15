package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Vehicle;
import com.badrulamin.University_Management.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Page<Vehicle> findAll(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle update(Long id, Vehicle vehicle) {
        findById(id);
        vehicle.setId(id);
        return vehicleRepository.save(vehicle);
    }

    public void delete(Long id) {
        findById(id);
        vehicleRepository.deleteById(id);
    }
}
