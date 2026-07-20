package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.ProgramSeatConfig;
import com.badrulamin.University_Management.entity.SeatAllocationConfig;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.ProgramSeatConfigRepository;
import com.badrulamin.University_Management.repository.SeatAllocationConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramSeatConfigService {

    private final ProgramSeatConfigRepository seatConfigRepository;
    private final SeatAllocationConfigRepository allocationConfigRepository;

    public List<ProgramSeatConfig> findByConfigId(Long configId) {
        return seatConfigRepository.findByConfig_Id(configId);
    }

    public List<ProgramSeatConfig> findActiveByConfigId(Long configId) {
        return seatConfigRepository.findByConfig_IdAndIsActive(configId, true);
    }

    public ProgramSeatConfig findById(Long id) {
        return seatConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramSeatConfig", "id", id));
    }

    public ProgramSeatConfig findByConfigAndProgramAndShift(Long configId, Long programId, String shift) {
        return seatConfigRepository.findByConfig_IdAndProgram_IdAndShift(configId, programId, shift)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramSeatConfig", "configId+programId+shift",
                        configId + "+" + programId + "+" + shift));
    }

    @Transactional
    public ProgramSeatConfig create(ProgramSeatConfig seatConfig) {
        SeatAllocationConfig config = allocationConfigRepository.findById(seatConfig.getConfigId())
                .orElseThrow(() -> new ResourceNotFoundException("SeatAllocationConfig", "id", seatConfig.getConfigId()));
        if ("ACTIVE".equals(config.getStatus())) {
            throw new BusinessException("Cannot modify seat configuration while allocation is active");
        }
        seatConfig.setAllocatedSeats(0);
        seatConfig.setWaitingSeats(0);
        seatConfig.setGeneralSeats(seatConfig.getTotalSeats() - seatConfig.getQuotaSeats() - seatConfig.getReservedSeats());
        return seatConfigRepository.save(seatConfig);
    }

    @Transactional
    public ProgramSeatConfig update(Long id, ProgramSeatConfig updated) {
        ProgramSeatConfig existing = findById(id);
        SeatAllocationConfig config = allocationConfigRepository.findById(existing.getConfigId())
                .orElseThrow(() -> new ResourceNotFoundException("SeatAllocationConfig", "id", existing.getConfigId()));
        if ("ACTIVE".equals(config.getStatus())) {
            throw new BusinessException("Cannot modify seat configuration while allocation is active");
        }
        existing.setFaculty(updated.getFaculty());
        existing.setDepartment(updated.getDepartment());
        existing.setProgram(updated.getProgram());
        existing.setShift(updated.getShift());
        existing.setTotalSeats(updated.getTotalSeats());
        existing.setQuotaSeats(updated.getQuotaSeats());
        existing.setReservedSeats(updated.getReservedSeats());
        existing.setGeneralSeats(updated.getTotalSeats() - updated.getQuotaSeats() - updated.getReservedSeats());
        return seatConfigRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        ProgramSeatConfig existing = findById(id);
        if (existing.getAllocatedSeats() > 0) {
            throw new BusinessException("Cannot delete seat configuration with allocated seats");
        }
        seatConfigRepository.deleteById(id);
    }

    public List<ProgramSeatConfig> findProgramsWithAvailableSeats(Long configId) {
        return seatConfigRepository.findProgramsWithAvailableSeats(configId);
    }

    public long getTotalSeats(Long configId) {
        return seatConfigRepository.sumTotalSeatsByConfig(configId);
    }

    public long getAllocatedSeats(Long configId) {
        return seatConfigRepository.sumAllocatedSeatsByConfig(configId);
    }
}
