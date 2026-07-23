package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionTest;
import com.badrulamin.University_Management.entity.PreAdmissionRegistration;
import com.badrulamin.University_Management.entity.SeatAllocation;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.AdmissionTestRepository;
import com.badrulamin.University_Management.repository.PreAdmissionRegistrationRepository;
import com.badrulamin.University_Management.repository.SeatAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatAllocationService {

    private final SeatAllocationRepository seatAllocationRepository;
    private final AdmissionTestRepository admissionTestRepository;
    private final PreAdmissionRegistrationRepository preAdmissionRegistrationRepository;

    public Page<SeatAllocation> findAll(Pageable pageable) {
        return seatAllocationRepository.findAll(pageable);
    }

    public SeatAllocation findById(Long id) {
        return seatAllocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SeatAllocation", "id", id));
    }

    public SeatAllocation save(SeatAllocation seatAllocation) {
        return seatAllocationRepository.save(seatAllocation);
    }

    public SeatAllocation update(Long id, SeatAllocation seatAllocation) {
        findById(id);
        seatAllocation.setId(id);
        return seatAllocationRepository.save(seatAllocation);
    }

    public void delete(Long id) {
        findById(id);
        seatAllocationRepository.deleteById(id);
    }

    public List<SeatAllocation> findByTestId(Long testId) {
        return seatAllocationRepository.findByTest_Id(testId);
    }

    public Optional<SeatAllocation> findByTestIdAndRegistrationId(Long testId, Long registrationId) {
        return seatAllocationRepository.findByTest_IdAndRegistration_Id(testId, registrationId);
    }

    public Optional<SeatAllocation> findByRollNumber(String rollNumber) {
        return seatAllocationRepository.findByRollNumber(rollNumber);
    }

    public long countByTestId(Long testId) {
        return seatAllocationRepository.countByTest_Id(testId);
    }

    public long countByTestIdAndStatus(Long testId, String status) {
        return seatAllocationRepository.countByTest_IdAndStatus(testId, status);
    }

    @Transactional
    public List<SeatAllocation> autoGenerateSeats(Long testId) {
        AdmissionTest test = admissionTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTest", "id", testId));

        List<String> eligibleStatuses = List.of("SUBMITTED", "ADMIT_CARD_GENERATED", "TEST_COMPLETED", "MERIT_PROCESSED");
        List<PreAdmissionRegistration> registrations = preAdmissionRegistrationRepository.findByStatusIn(eligibleStatuses);

        List<SeatAllocation> allocations = new ArrayList<>();
        int rollSequence = 1;
        int roomSeatSequence = 1;
        String lastRoom = null;
        int year = Year.now().getValue();

        for (PreAdmissionRegistration registration : registrations) {
            Optional<SeatAllocation> existing = seatAllocationRepository.findByTest_IdAndRegistration_Id(testId, registration.getId());
            if (existing.isPresent()) {
                continue;
            }

            SeatAllocation allocation = new SeatAllocation();
            allocation.setTest(test);
            allocation.setRegistration(registration);
            allocation.setCenterName(test.getExamCenter());
            allocation.setBuildingName(test.getBuilding());
            allocation.setRoomName(test.getRoom());

            String rollNumber = "ROLL-" + year + "-" + String.format("%05d", rollSequence++);
            allocation.setRollNumber(rollNumber);

            String currentRoom = test.getRoom() != null ? test.getRoom() : "DEFAULT";
            if (!currentRoom.equals(lastRoom)) {
                Long maxSeat = seatAllocationRepository.findMaxSeatSequenceInRoom(testId, currentRoom);
                roomSeatSequence = (maxSeat != null ? maxSeat.intValue() : 0) + 1;
                lastRoom = currentRoom;
            }
            String seatNumber = "S" + String.format("%04d", roomSeatSequence++);
            allocation.setSeatNumber(seatNumber);
            allocation.setStatus("ASSIGNED");

            allocations.add(seatAllocationRepository.save(allocation));
        }

        return allocations;
    }
}