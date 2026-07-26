package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionTest;
import com.badrulamin.University_Management.entity.AdmitCard;
import com.badrulamin.University_Management.entity.SeatAllocation;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.AdmissionTestRepository;
import com.badrulamin.University_Management.repository.AdmitCardRepository;
import com.badrulamin.University_Management.repository.SeatAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdmitCardService {

    private final AdmitCardRepository admitCardRepository;
    private final AdmissionTestRepository admissionTestRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final NotificationHelper notificationHelper;

    public Page<AdmitCard> findAll(Pageable pageable) {
        return admitCardRepository.findAll(pageable);
    }

    public AdmitCard findById(Long id) {
        return admitCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdmitCard", "id", id));
    }

    @Transactional
    public AdmitCard save(AdmitCard admitCard) {
        return admitCardRepository.save(admitCard);
    }

    @Transactional
    public AdmitCard update(Long id, AdmitCard admitCard) {
        findById(id);
        admitCard.setId(id);
        return admitCardRepository.save(admitCard);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        admitCardRepository.deleteById(id);
    }

    public List<AdmitCard> findByTestId(Long testId) {
        return admitCardRepository.findByTest_Id(testId);
    }

    public List<AdmitCard> findByRegistrationId(Long registrationId) {
        return admitCardRepository.findByRegistration_Id(registrationId);
    }

    public Optional<AdmitCard> findByTestIdAndRegistrationId(Long testId, Long registrationId) {
        return admitCardRepository.findByTest_IdAndRegistration_Id(testId, registrationId);
    }

    public Optional<AdmitCard> findByAdmitCardNumber(String admitCardNumber) {
        return admitCardRepository.findByAdmitCardNumber(admitCardNumber);
    }

    public long countByTestId(Long testId) {
        return admitCardRepository.countByTest_Id(testId);
    }

    public long countByTestIdAndStatus(Long testId, String status) {
        return admitCardRepository.countByTest_IdAndStatus(testId, status);
    }

    @Transactional
    public List<AdmitCard> generateAdmitCards(Long testId) {
        AdmissionTest test = admissionTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTest", "id", testId));

        List<SeatAllocation> seatAllocations = seatAllocationRepository.findByTest_Id(testId);
        List<AdmitCard> admitCards = new ArrayList<>();
        int year = Year.now().getValue();

        Long maxSequence = admitCardRepository.findMaxAdmitCardSequence(testId);
        int sequence = (maxSequence != null ? maxSequence.intValue() : 0) + 1;

        for (SeatAllocation allocation : seatAllocations) {
            Optional<AdmitCard> existing = admitCardRepository.findByTest_IdAndRegistration_Id(testId, allocation.getRegistration().getId());
            if (existing.isPresent()) {
                continue;
            }

            AdmitCard admitCard = new AdmitCard();
            admitCard.setTest(test);
            admitCard.setRegistration(allocation.getRegistration());
            admitCard.setRollNumber(allocation.getRollNumber());
            admitCard.setSeatNumber(allocation.getSeatNumber());
            admitCard.setCenterName(allocation.getCenterName());
            admitCard.setBuildingName(allocation.getBuildingName());
            admitCard.setRoomName(allocation.getRoomName());

            String admitCardNumber = "AC-" + year + "-" + String.format("%06d", sequence++);
            admitCard.setAdmitCardNumber(admitCardNumber);
            admitCard.setQrCode(admitCardNumber);
            admitCard.setIssuedAt(LocalDateTime.now());
            admitCard.setStatus("GENERATED");

            admitCards.add(admitCardRepository.save(admitCard));

            notificationHelper.admitCardGenerated(
                allocation.getRegistration().getId(),
                test.getName(),
                admitCardNumber
            );
        }

        return admitCards;
    }
}