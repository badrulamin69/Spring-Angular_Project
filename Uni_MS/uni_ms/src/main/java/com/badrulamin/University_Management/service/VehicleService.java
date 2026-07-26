package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Vehicle;
import com.badrulamin.University_Management.repository.VehicleRepository;
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
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Vehicle> findAll(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
    }

    @Transactional
    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle update(Long id, Vehicle incoming) {
        Vehicle existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return vehicleRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        vehicleRepository.deleteById(id);
    }
}