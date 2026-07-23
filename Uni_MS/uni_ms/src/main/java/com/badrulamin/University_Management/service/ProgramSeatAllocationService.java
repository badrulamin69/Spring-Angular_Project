package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

    @Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramSeatAllocationService {

    private final DepartmentAllocationRepository allocationRepository;
    private final SeatAllocationConfigRepository configRepository;
    private final ProgramSeatConfigRepository seatConfigRepository;
    private final ApplicantChoiceSubmissionRepository submissionRepository;
    private final ApplicantChoiceRepository choiceRepository;
    private final PreAdmissionRegistrationRepository registrationRepository;
    private final SeatAllocationLogRepository logRepository;

    public Page<DepartmentAllocation> findByFilters(Long configId, String search, String status,
                                                     Long programId, Long facultyId, Boolean isWaiting,
                                                     Pageable pageable) {
        return allocationRepository.findByFilters(configId, search, status, programId, facultyId, isWaiting, pageable);
    }

    public DepartmentAllocation findById(Long id) {
        return allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SeatAllocation", "id", id));
    }

    public DepartmentAllocation findByRegistrationAndConfig(Long registrationId, Long configId) {
        return allocationRepository.findByConfig_IdAndRegistration_Id(configId, registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("SeatAllocation", "registrationId+configId",
                        registrationId + "+" + configId));
    }

    public List<DepartmentAllocation> findByConfigId(Long configId) {
        return allocationRepository.findByConfig_IdOrderByMeritRankAsc(configId);
    }

    public Map<String, Object> getStats(Long configId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", allocationRepository.countByConfig_Id(configId));
        stats.put("allocated", allocationRepository.countByConfig_IdAndStatus(configId, "ALLOCATED"));
        stats.put("confirmed", allocationRepository.countByConfig_IdAndStatus(configId, "CONFIRMED"));
        stats.put("declined", allocationRepository.countByConfig_IdAndStatus(configId, "DECLINED"));
        stats.put("cancelled", allocationRepository.countByConfig_IdAndStatus(configId, "CANCELLED"));
        stats.put("expired", allocationRepository.countByConfig_IdAndStatus(configId, "EXPIRED"));
        stats.put("waiting", allocationRepository.countByConfigAndStatusAndWaiting(configId, "ALLOCATED", true));
        stats.put("notAllocated", allocationRepository.countByConfig_IdAndStatus(configId, "NOT_ALLOCATED"));

        long totalSeats = seatConfigRepository.sumTotalSeatsByConfig(configId);
        long allocatedSeats = seatConfigRepository.sumAllocatedSeatsByConfig(configId);
        stats.put("totalSeats", totalSeats);
        stats.put("allocatedSeats", allocatedSeats);
        stats.put("remainingSeats", totalSeats - allocatedSeats);
        stats.put("utilizationPercent", totalSeats > 0 ? Math.round((double) allocatedSeats / totalSeats * 100) : 0);

        return stats;
    }

    @Transactional
    public Map<String, Object> runAutoAllocation(Long configId) {
        SeatAllocationConfig config = configRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("SeatAllocationConfig", "id", configId));

        if (!"ACTIVE".equals(config.getStatus())) {
            throw new BusinessException("Configuration must be ACTIVE to run allocation");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(config.getAllocationStartDate()) || now.isAfter(config.getAllocationEndDate())) {
            throw new BusinessException("Allocation window is not open");
        }

        List<DepartmentAllocation> existing = allocationRepository.findByConfig_Id(configId);
        if (!existing.isEmpty()) {
            allocationRepository.deleteAllInBatch(existing);
            seatConfigRepository.findByConfig_Id(configId).forEach(sc -> {
                sc.setAllocatedSeats(0);
                sc.setWaitingSeats(0);
                seatConfigRepository.save(sc);
            });
        }

        List<ApplicantChoiceSubmission> submissions = new ArrayList<>();
        submissions.addAll(submissionRepository.findByConfig_IdAndStatus(configId, "SUBMITTED"));
        submissions.addAll(submissionRepository.findByConfig_IdAndStatus(configId, "LOCKED"));

        List<DepartmentAllocation> allAllocations = new ArrayList<>();

        submissions.sort((a, b) -> {
            if (a.getMeritRank() == null && b.getMeritRank() == null) return 0;
            if (a.getMeritRank() == null) return 1;
            if (b.getMeritRank() == null) return -1;
            return a.getMeritRank().compareTo(b.getMeritRank());
        });

        for (ApplicantChoiceSubmission submission : submissions) {
            DepartmentAllocation allocation = allocateForApplicant(submission, config, config.getAllocationRound());
            allAllocations.add(allocation);
        }

        List<DepartmentAllocation> saved = allocationRepository.saveAll(allAllocations);

        long allocated = saved.stream().filter(a -> "ALLOCATED".equals(a.getStatus()) && !Boolean.TRUE.equals(a.getIsWaiting())).count();
        long waiting = saved.stream().filter(a -> Boolean.TRUE.equals(a.getIsWaiting())).count();
        long notAllocated = saved.stream().filter(a -> "NOT_ALLOCATED".equals(a.getStatus())).count();

        Map<String, Object> result = new HashMap<>();
        result.put("totalProcessed", submissions.size());
        result.put("allocated", allocated);
        result.put("waiting", waiting);
        result.put("notAllocated", notAllocated);
        result.put("round", config.getAllocationRound());

        log.info("Auto allocation completed for config {}: allocated={}, waiting={}, notAllocated={}",
                configId, allocated, waiting, notAllocated);
        return result;
    }

    private DepartmentAllocation allocateForApplicant(ApplicantChoiceSubmission submission,
                                                       SeatAllocationConfig config, int round) {
        PreAdmissionRegistration registration = submission.getRegistration();
        List<ApplicantChoice> choices = choiceRepository.findBySubmission_IdOrderByPriorityAsc(submission.getId());

        DepartmentAllocation allocation = new DepartmentAllocation();
        allocation.setConfig(config);
        allocation.setRegistration(registration);
        allocation.setAllocationRound(round);
        allocation.setMeritRank(submission.getMeritRank());
        allocation.setTotalScore(submission.getMeritScore());
        allocation.setAllocatedAt(LocalDateTime.now());
        allocation.setDeadline(LocalDateTime.now().plusHours(config.getAcceptDeadlineHours()));
        allocation.setAllocationNumber(generateAllocationNumber(registration));
        allocation.setStatus("NOT_ALLOCATED");

        for (ApplicantChoice choice : choices) {
            String shift = choice.getShift() != null ? choice.getShift() : "DAY";
            ProgramSeatConfig seatConfig = seatConfigRepository
                    .findByConfig_IdAndProgram_IdAndShift(config.getId(), choice.getProgramId(), shift)
                    .orElse(null);

            if (seatConfig == null) continue;

            int available = seatConfig.getTotalSeats() - seatConfig.getAllocatedSeats();
            if (available > 0) {
                allocation.setAllocatedProgram(choice.getProgram());
                allocation.setAllocatedDepartment(choice.getDepartment());
                allocation.setAllocatedFaculty(choice.getFaculty());
                allocation.setShift(shift);
                allocation.setChoiceNumber(choice.getPriority());
                allocation.setIsWaiting(false);
                allocation.setStatus("ALLOCATED");
                seatConfig.setAllocatedSeats(seatConfig.getAllocatedSeats() + 1);
                seatConfigRepository.save(seatConfig);
                return allocation;
            } else {
                long waitingCount = allocationRepository.countByConfig_IdAndAllocatedProgram_IdAndStatus(
                        config.getId(), choice.getProgramId(), "ALLOCATED");
                int maxWaiting = Math.max(1, seatConfig.getTotalSeats() / 2);
                if (waitingCount < maxWaiting) {
                    allocation.setAllocatedProgram(choice.getProgram());
                    allocation.setAllocatedDepartment(choice.getDepartment());
                    allocation.setAllocatedFaculty(choice.getFaculty());
                    allocation.setShift(shift);
                    allocation.setChoiceNumber(choice.getPriority());
                    allocation.setIsWaiting(true);
                    allocation.setWaitingRank((int) waitingCount + 1);
                    allocation.setStatus("ALLOCATED");
                    seatConfig.setWaitingSeats(seatConfig.getWaitingSeats() + 1);
                    seatConfigRepository.save(seatConfig);
                    return allocation;
                }
            }
        }

        allocation.setStatus("NOT_ALLOCATED");
        allocation.setIsWaiting(false);
        return allocation;
    }

    @Transactional
    public DepartmentAllocation manualAllocate(Long registrationId, Long programId, Long configId, String shift, String remarks) {
        SeatAllocationConfig config = configRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("SeatAllocationConfig", "id", configId));
        if (!config.getManualAllocation()) {
            throw new BusinessException("Manual allocation is not enabled");
        }

        PreAdmissionRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("PreAdmissionRegistration", "id", registrationId));

        ProgramSeatConfig seatConfig = seatConfigRepository
                .findByConfig_IdAndProgram_IdAndShift(configId, programId, shift)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramSeatConfig", "configId+programId+shift",
                        configId + "+" + programId + "+" + shift));

        int available = seatConfig.getTotalSeats() - seatConfig.getAllocatedSeats();
        if (available <= 0) {
            throw new BusinessException("No seats available for this program");
        }

        Optional<DepartmentAllocation> existing = allocationRepository.findByConfig_IdAndRegistration_Id(configId, registrationId);
        if (existing.isPresent()) {
            DepartmentAllocation old = existing.get();
            if ("ALLOCATED".equals(old.getStatus()) && !Boolean.TRUE.equals(old.getIsWaiting())) {
                ProgramSeatConfig oldSeat = seatConfigRepository
                        .findByConfig_IdAndProgram_IdAndShift(configId, old.getAllocatedProgramId(), old.getShift())
                        .orElse(null);
                if (oldSeat != null) {
                    oldSeat.setAllocatedSeats(Math.max(0, oldSeat.getAllocatedSeats() - 1));
                    seatConfigRepository.save(oldSeat);
                }
            } else if (Boolean.TRUE.equals(old.getIsWaiting())) {
                ProgramSeatConfig oldSeat = seatConfigRepository
                        .findByConfig_IdAndProgram_IdAndShift(configId, old.getAllocatedProgramId(), old.getShift())
                        .orElse(null);
                if (oldSeat != null) {
                    oldSeat.setWaitingSeats(Math.max(0, oldSeat.getWaitingSeats() - 1));
                    seatConfigRepository.save(oldSeat);
                }
            }
            old.setAllocatedProgram(seatConfig.getProgram());
            old.setAllocatedDepartment(seatConfig.getDepartment());
            old.setAllocatedFaculty(seatConfig.getFaculty());
            old.setShift(shift);
            old.setStatus("ALLOCATED");
            old.setIsWaiting(false);
            old.setAllocatedAt(LocalDateTime.now());
            old.setDeadline(LocalDateTime.now().plusHours(config.getAcceptDeadlineHours()));
            old.setRemarks(remarks);
            seatConfig.setAllocatedSeats(seatConfig.getAllocatedSeats() + 1);
            seatConfigRepository.save(seatConfig);
            createLog(old, "MANUAL_ALLOCATED", remarks);
            return allocationRepository.save(old);
        }

        DepartmentAllocation allocation = new DepartmentAllocation();
        allocation.setAllocationNumber(generateAllocationNumber(registration));
        allocation.setConfig(config);
        allocation.setRegistration(registration);
        allocation.setAllocatedProgram(seatConfig.getProgram());
        allocation.setAllocatedDepartment(seatConfig.getDepartment());
        allocation.setAllocatedFaculty(seatConfig.getFaculty());
        allocation.setShift(shift);
        allocation.setStatus("ALLOCATED");
        allocation.setIsWaiting(false);
        allocation.setAllocatedAt(LocalDateTime.now());
        allocation.setDeadline(LocalDateTime.now().plusHours(config.getAcceptDeadlineHours()));
        allocation.setAllocationRound(config.getAllocationRound());
        allocation.setRemarks(remarks);
        seatConfig.setAllocatedSeats(seatConfig.getAllocatedSeats() + 1);
        seatConfigRepository.save(seatConfig);
        DepartmentAllocation saved = allocationRepository.save(allocation);
        createLog(saved, "MANUAL_ALLOCATED", remarks);
        return saved;
    }

    @Transactional
    public DepartmentAllocation changeAllocation(Long allocationId, Long newProgramId, String shift, String remarks) {
        DepartmentAllocation allocation = findById(allocationId);
        if (!"ALLOCATED".equals(allocation.getStatus()) || Boolean.TRUE.equals(allocation.getIsWaiting())) {
            throw new BusinessException("Only allocated (non-waiting) allocations can be changed");
        }

        ProgramSeatConfig oldSeat = seatConfigRepository
                .findByConfig_IdAndProgram_IdAndShift(allocation.getConfigId(), allocation.getAllocatedProgramId(), allocation.getShift())
                .orElse(null);
        if (oldSeat != null) {
            oldSeat.setAllocatedSeats(Math.max(0, oldSeat.getAllocatedSeats() - 1));
            seatConfigRepository.save(oldSeat);
        }

        ProgramSeatConfig newSeat = seatConfigRepository
                .findByConfig_IdAndProgram_IdAndShift(allocation.getConfigId(), newProgramId, shift)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramSeatConfig", "configId+programId+shift",
                        allocation.getConfigId() + "+" + newProgramId + "+" + shift));

        int available = newSeat.getTotalSeats() - newSeat.getAllocatedSeats();
        if (available <= 0) {
            throw new BusinessException("No seats available in the new program");
        }

        SeatAllocationConfig config = configRepository.findById(allocation.getConfigId())
                .orElseThrow(() -> new ResourceNotFoundException("SeatAllocationConfig", "id", allocation.getConfigId()));

        allocation.setAllocatedProgram(newSeat.getProgram());
        allocation.setAllocatedDepartment(newSeat.getDepartment());
        allocation.setAllocatedFaculty(newSeat.getFaculty());
        allocation.setShift(shift);
        allocation.setAllocatedAt(LocalDateTime.now());
        allocation.setDeadline(LocalDateTime.now().plusHours(config.getAcceptDeadlineHours()));
        allocation.setRemarks(remarks);

        newSeat.setAllocatedSeats(newSeat.getAllocatedSeats() + 1);
        seatConfigRepository.save(newSeat);

        createLog(allocation, "CHANGED", remarks);
        return allocationRepository.save(allocation);
    }

    @Transactional
    public DepartmentAllocation cancelAllocation(Long allocationId, String remarks) {
        DepartmentAllocation allocation = findById(allocationId);
        if ("CANCELLED".equals(allocation.getStatus())) {
            throw new BusinessException("Allocation is already cancelled");
        }

        String previousStatus = allocation.getStatus();
        allocation.setStatus("CANCELLED");
        allocation.setRemarks(remarks);

        if ("ALLOCATED".equals(previousStatus) && !Boolean.TRUE.equals(allocation.getIsWaiting())) {
            ProgramSeatConfig seatConfig = seatConfigRepository
                    .findByConfig_IdAndProgram_IdAndShift(allocation.getConfigId(), allocation.getAllocatedProgramId(), allocation.getShift())
                    .orElse(null);
            if (seatConfig != null) {
                seatConfig.setAllocatedSeats(Math.max(0, seatConfig.getAllocatedSeats() - 1));
                seatConfigRepository.save(seatConfig);
                promoteFromWaiting(allocation.getConfigId(), allocation.getAllocatedProgramId(), allocation.getShift());
            }
        } else if (Boolean.TRUE.equals(allocation.getIsWaiting())) {
            ProgramSeatConfig seatConfig = seatConfigRepository
                    .findByConfig_IdAndProgram_IdAndShift(allocation.getConfigId(), allocation.getAllocatedProgramId(), allocation.getShift())
                    .orElse(null);
            if (seatConfig != null) {
                seatConfig.setWaitingSeats(Math.max(0, seatConfig.getWaitingSeats() - 1));
                seatConfigRepository.save(seatConfig);
            }
        }

        createLog(allocation, "CANCELLED", remarks);
        return allocationRepository.save(allocation);
    }

    @Transactional
    public DepartmentAllocation acceptAllocation(Long allocationId) {
        DepartmentAllocation allocation = findById(allocationId);
        if (!"ALLOCATED".equals(allocation.getStatus())) {
            throw new BusinessException("Only ALLOCATED seats can be accepted");
        }
        if (Boolean.TRUE.equals(allocation.getIsWaiting())) {
            throw new BusinessException("Waiting list allocations cannot be accepted");
        }
        allocation.setStatus("CONFIRMED");
        allocation.setAcceptedAt(LocalDateTime.now());
        allocation.setConfirmedAt(LocalDateTime.now());
        createLog(allocation, "ACCEPTED", "Applicant accepted the seat");
        return allocationRepository.save(allocation);
    }

    @Transactional
    public DepartmentAllocation declineAllocation(Long allocationId, String remarks) {
        DepartmentAllocation allocation = findById(allocationId);
        if (!"ALLOCATED".equals(allocation.getStatus())) {
            throw new BusinessException("Only ALLOCATED seats can be declined");
        }
        allocation.setStatus("DECLINED");
        allocation.setDeclinedAt(LocalDateTime.now());
        allocation.setRemarks(remarks);

        if (!Boolean.TRUE.equals(allocation.getIsWaiting())) {
            ProgramSeatConfig seatConfig = seatConfigRepository
                    .findByConfig_IdAndProgram_IdAndShift(allocation.getConfigId(), allocation.getAllocatedProgramId(), allocation.getShift())
                    .orElse(null);
            if (seatConfig != null) {
                seatConfig.setAllocatedSeats(Math.max(0, seatConfig.getAllocatedSeats() - 1));
                seatConfigRepository.save(seatConfig);
                promoteFromWaiting(allocation.getConfigId(), allocation.getAllocatedProgramId(), allocation.getShift());
            }
        }

        createLog(allocation, "DECLINED", remarks);
        return allocationRepository.save(allocation);
    }

    @Transactional
    public void promoteFromWaiting(Long configId, Long programId, String shift) {
        List<DepartmentAllocation> waitingList = allocationRepository
                .findByConfig_IdAndAllocatedProgram_IdAndIsWaitingTrueOrderByWaitingRankAsc(configId, programId);

        if (waitingList.isEmpty()) return;

        ProgramSeatConfig seatConfig = seatConfigRepository
                .findByConfig_IdAndProgram_IdAndShift(configId, programId, shift)
                .orElse(null);
        if (seatConfig == null) return;

        int available = seatConfig.getTotalSeats() - seatConfig.getAllocatedSeats();
        if (available <= 0) return;

        SeatAllocationConfig config = configRepository.findById(configId).orElse(null);
        int deadlineHours = config != null ? config.getAcceptDeadlineHours() : 72;

        DepartmentAllocation promoted = waitingList.get(0);
        promoted.setIsWaiting(false);
        promoted.setWaitingRank(null);
        promoted.setChoiceNumber(null);
        promoted.setAllocatedAt(LocalDateTime.now());
        promoted.setDeadline(LocalDateTime.now().plusHours(deadlineHours));
        promoted.setRemarks("Promoted from waiting list");

        seatConfig.setWaitingSeats(Math.max(0, seatConfig.getWaitingSeats() - 1));
        seatConfig.setAllocatedSeats(seatConfig.getAllocatedSeats() + 1);
        seatConfigRepository.save(seatConfig);

        allocationRepository.save(promoted);
        createLog(promoted, "PROMOTED", "Promoted from waiting list");
    }

    @Transactional
    public Map<String, Object> runReallocation(Long configId) {
        SeatAllocationConfig config = configRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("SeatAllocationConfig", "id", configId));

        int newRound = config.getAllocationRound() + 1;
        config.setAllocationRound(newRound);
        configRepository.save(config);

        List<DepartmentAllocation> expired = allocationRepository.findByConfig_Id(configId).stream()
                .filter(a -> "ALLOCATED".equals(a.getStatus()) && !Boolean.TRUE.equals(a.getIsWaiting()))
                .filter(a -> a.getDeadline() != null && a.getDeadline().isBefore(LocalDateTime.now()))
                .toList();

        for (DepartmentAllocation allocation : expired) {
            allocation.setStatus("EXPIRED");
            allocation.setRemarks("Deadline expired - released for reallocation round " + newRound);
            allocationRepository.save(allocation);

            ProgramSeatConfig seatConfig = seatConfigRepository
                    .findByConfig_IdAndProgram_IdAndShift(configId, allocation.getAllocatedProgramId(), allocation.getShift())
                    .orElse(null);
            if (seatConfig != null) {
                seatConfig.setAllocatedSeats(Math.max(0, seatConfig.getAllocatedSeats() - 1));
                seatConfigRepository.save(seatConfig);
            }
        }

        List<DepartmentAllocation> waitingList = allocationRepository
                .findByConfig_IdAndIsWaitingTrueOrderByWaitingRankAsc(configId);

        int promotedCount = 0;
        for (DepartmentAllocation waiting : waitingList) {
            ProgramSeatConfig seatConfig = seatConfigRepository
                    .findByConfig_IdAndProgram_IdAndShift(configId, waiting.getAllocatedProgramId(), waiting.getShift())
                    .orElse(null);
            if (seatConfig != null) {
                int available = seatConfig.getTotalSeats() - seatConfig.getAllocatedSeats();
                if (available > 0) {
                    waiting.setIsWaiting(false);
                    waiting.setWaitingRank(null);
                    waiting.setAllocatedAt(LocalDateTime.now());
                    waiting.setDeadline(LocalDateTime.now().plusHours(config.getAcceptDeadlineHours()));
                    waiting.setAllocationRound(newRound);
                    waiting.setRemarks("Reallocated in round " + newRound);
                    seatConfig.setWaitingSeats(Math.max(0, seatConfig.getWaitingSeats() - 1));
                    seatConfig.setAllocatedSeats(seatConfig.getAllocatedSeats() + 1);
                    seatConfigRepository.save(seatConfig);
                    allocationRepository.save(waiting);
                    createLog(waiting, "REALLOCATED", "Reallocated in round " + newRound);
                    promotedCount++;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("expiredCount", expired.size());
        result.put("waitingCount", waitingList.size());
        result.put("promotedCount", promotedCount);
        result.put("newRound", newRound);
        return result;
    }

    @Transactional
    public void expireOverdueAllocations(Long configId) {
        List<DepartmentAllocation> allocations = allocationRepository.findByConfig_Id(configId).stream()
                .filter(a -> "ALLOCATED".equals(a.getStatus()) && !Boolean.TRUE.equals(a.getIsWaiting()))
                .filter(a -> a.getDeadline() != null && a.getDeadline().isBefore(LocalDateTime.now()))
                .toList();

        for (DepartmentAllocation allocation : allocations) {
            allocation.setStatus("EXPIRED");
            allocation.setRemarks("Deadline expired");
            allocationRepository.save(allocation);

            ProgramSeatConfig seatConfig = seatConfigRepository
                    .findByConfig_IdAndProgram_IdAndShift(configId, allocation.getAllocatedProgramId(), allocation.getShift())
                    .orElse(null);
            if (seatConfig != null) {
                seatConfig.setAllocatedSeats(Math.max(0, seatConfig.getAllocatedSeats() - 1));
                seatConfigRepository.save(seatConfig);
                promoteFromWaiting(configId, allocation.getAllocatedProgramId(), allocation.getShift());
            }
        }
    }

    public List<Map<String, Object>> getDepartmentDemandReport(Long configId) {
        List<ProgramSeatConfig> seatConfigs = seatConfigRepository.findByConfig_IdAndIsActive(configId, true);
        List<Map<String, Object>> report = new ArrayList<>();

        for (ProgramSeatConfig seatConfig : seatConfigs) {
            Map<String, Object> row = new HashMap<>();
            row.put("programId", seatConfig.getProgramId());
            row.put("programName", seatConfig.getProgram() != null ? seatConfig.getProgram().getName() : "");
            row.put("departmentName", seatConfig.getDepartment() != null ? seatConfig.getDepartment().getName() : "");
            row.put("facultyName", seatConfig.getFaculty() != null ? seatConfig.getFaculty().getName() : "");
            row.put("shift", seatConfig.getShift());
            row.put("totalSeats", seatConfig.getTotalSeats());
            row.put("allocatedSeats", seatConfig.getAllocatedSeats());
            row.put("waitingSeats", seatConfig.getWaitingSeats());
            row.put("remainingSeats", seatConfig.getTotalSeats() - seatConfig.getAllocatedSeats());
            long allocated = allocationRepository.countByConfig_IdAndAllocatedProgram_Id(configId, seatConfig.getProgramId());
            row.put("totalApplicants", allocated);
            report.add(row);
        }

        report.sort((a, b) -> Long.compare((long) b.get("totalApplicants"), (long) a.get("totalApplicants")));
        return report;
    }

    private void createLog(DepartmentAllocation allocation, String action, String remarks) {
        SeatAllocationLog logEntry = new SeatAllocationLog();
        logEntry.setAllocation(allocation);
        logEntry.setAction(action);
        logEntry.setNewStatus(allocation.getStatus());
        logEntry.setRemarks(remarks);
        logEntry.setPerformedAt(LocalDateTime.now());
        logRepository.save(logEntry);
    }

    private String generateAllocationNumber(PreAdmissionRegistration registration) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ALLOC-" + registration.getRegistrationNumber() + "-" + timestamp;
    }
}