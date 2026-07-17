package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.entity.PreAdmissionRegistration;
import com.badrulamin.University_Management.entity.AdmissionTestResult;
import com.badrulamin.University_Management.entity.DepartmentAllocation;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.entity.Role;
import com.badrulamin.University_Management.repository.PreAdmissionRegistrationRepository;
import com.badrulamin.University_Management.repository.AdmissionTestResultRepository;
import com.badrulamin.University_Management.repository.DepartmentAllocationRepository;
import com.badrulamin.University_Management.repository.UserRepository;
import com.badrulamin.University_Management.repository.RoleRepository;
import com.badrulamin.University_Management.repository.ProgramRepository;
import com.badrulamin.University_Management.entity.Program;
import com.badrulamin.University_Management.entity.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreAdmissionRegistrationService {

    private final PreAdmissionRegistrationRepository repository;
    private final AdmissionTestResultRepository testResultRepository;
    private final DepartmentAllocationRepository allocationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProgramRepository programRepository;
    private static final SecureRandom secureRandom = new SecureRandom();

    public Page<PreAdmissionRegistration> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public PreAdmissionRegistration findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreAdmissionRegistration", "id", id));
    }

    public PreAdmissionRegistration findByRegistrationNumber(String registrationNumber) {
        return repository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("PreAdmissionRegistration", "registrationNumber", registrationNumber));
    }

    @Transactional
    public Map<String, Object> saveWithUserAccount(PreAdmissionRegistration registration) {
        if (registration.getEmail() != null && existsByEmail(registration.getEmail())) {
            throw new IllegalArgumentException("An application with this email already exists. Please use a different email or check your existing application status.");
        }
        if (registration.getEmail() != null && userRepository.existsByEmail(registration.getEmail())) {
            throw new IllegalArgumentException("A user account with this email already exists. Please use a different email.");
        }
        if (registration.getEmail() != null && userRepository.existsByUsername(registration.getEmail())) {
            throw new IllegalArgumentException("A user account with this username already exists. Please use a different email.");
        }
        if (registration.getRegistrationNumber() == null) {
            String prefix = "PRE-ADM-" + Year.now().getValue() + "-";
            long nextNum = repository.findMaxSequenceByRegistrationNumberPrefix(prefix) + 1;
            registration.setRegistrationNumber(prefix + String.format("%05d", nextNum));
        }
        if (registration.getStatus() == null) {
            registration.setStatus("SUBMITTED");
        }
        PreAdmissionRegistration saved = repository.save(registration);

        String tempPassword = generateTempPassword();
        String username = registration.getEmail();

        User user = new User();
        user.setUsername(username);
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setFirstName(registration.getFirstName());
        user.setLastName(registration.getLastName());
        user.setPhone(registration.getPhone());
        user.setActive(true);
        user.setEmailVerified(false);

        roleRepository.findByCode("ROLE_APPLICANT").ifPresent(user::addRole);

        userRepository.save(user);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", saved.getId());
        result.put("registrationNumber", saved.getRegistrationNumber());
        result.put("firstName", saved.getFirstName());
        result.put("lastName", saved.getLastName());
        result.put("email", saved.getEmail());
        result.put("status", saved.getStatus());
        result.put("loginEmail", username);
        result.put("tempPassword", tempPassword);
        result.put("message", "Registration successful. Use the email and temporary password above to login.");
        return result;
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public PreAdmissionRegistration update(Long id, PreAdmissionRegistration registration) {
        PreAdmissionRegistration existing = findById(id);
        registration.setId(id);
        registration.setRegistrationNumber(existing.getRegistrationNumber());
        registration.setCreatedAt(existing.getCreatedAt());
        return repository.save(registration);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    public PreAdmissionRegistration approve(Long id) {
        PreAdmissionRegistration reg = findById(id);
        reg.setStatus("ADMIT_CARD_GENERATED");
        return repository.save(reg);
    }

    public PreAdmissionRegistration reject(Long id, String remarks) {
        PreAdmissionRegistration reg = findById(id);
        reg.setStatus("REJECTED");
        reg.setRemarks(remarks);
        return repository.save(reg);
    }

    public long countByStatus(String status) {
        return repository.countByStatus(status);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Transactional
    public Map<String, Object> processMerit() {
        List<PreAdmissionRegistration> registrations = repository.findByStatusIn(
                List.of("ADMIT_CARD_GENERATED", "TEST_COMPLETED", "MERIT_PROCESSED"));

        Map<Long, Double> scores = new LinkedHashMap<>();
        for (PreAdmissionRegistration reg : registrations) {
            double ssc = reg.getSscGpa() != null ? reg.getSscGpa() : 0.0;
            double hsc = reg.getHscGpa() != null ? reg.getHscGpa() : 0.0;

            double testScore = 0.0;
            Optional<AdmissionTestResult> testResult = testResultRepository.findByRegistration_Id(reg.getId());
            if (testResult.isPresent()) {
                testScore = testResult.get().getTotalWeightedScore() != null ? testResult.get().getTotalWeightedScore() : 0.0;
            }

            double totalScore = (ssc * 10 * 0.30) + (hsc * 10 * 0.30) + (testScore * 0.40);
            scores.put(reg.getId(), totalScore);
        }

        List<Map.Entry<Long, Double>> sorted = scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        int rank = 1;
        int allocationsCreated = 0;
        Map<String, Integer> departmentWise = new LinkedHashMap<>();
        for (Map.Entry<Long, Double> entry : sorted) {
            PreAdmissionRegistration reg = repository.findById(entry.getKey()).orElse(null);
            if (reg == null) continue;

            Optional<DepartmentAllocation> existing = allocationRepository.findByRegistration_Id(reg.getId());
            DepartmentAllocation allocation = existing.orElse(new DepartmentAllocation());
            if (!existing.isPresent()) {
                long allocNum = allocationRepository.count() + 1;
                allocation.setAllocationNumber("ALLOC-" + Year.now().getValue() + "-" + String.format("%05d", allocNum));
                allocation.setAllocatedAt(LocalDateTime.now());
            }
            allocation.setRegistration(reg);
            allocation.setTotalScore(entry.getValue());
            allocation.setMeritRank(rank++);
            allocation.setStatus("ALLOCATED");

            String deptName = "Unassigned";
            if (reg.getProgramPreference1() != null) {
                String pref = reg.getProgramPreference1().trim();
                programRepository.findByCode(pref)
                        .or(() -> programRepository.findAll().stream()
                                .filter(p -> p.getName().equalsIgnoreCase(pref)
                                        || p.getName().toLowerCase().contains(pref.toLowerCase())
                                        || pref.toLowerCase().contains(p.getName().toLowerCase()))
                                .findFirst())
                        .ifPresent(program -> {
                            allocation.setAllocatedProgram(program);
                            Department dept = program.getDepartment();
                            if (dept != null) {
                                allocation.setAllocatedDepartment(dept);
                            }
                        });
            }

            allocationRepository.save(allocation);

            reg.setStatus("MERIT_PROCESSED");
            repository.save(reg);
            allocationsCreated++;

            if (allocation.getAllocatedDepartment() != null) {
                deptName = allocation.getAllocatedDepartment().getName();
            }
            departmentWise.merge(deptName, 1, Integer::sum);
        }

        return Map.of(
                "totalProcessed", registrations.size(),
                "allocationsCreated", allocationsCreated,
                "departmentWise", departmentWise,
                "message", "Merit processing completed. " + allocationsCreated + " allocations created."
        );
    }

    public Map<String, Object> getMeritPreview() {
        List<PreAdmissionRegistration> eligible = repository.findByStatusIn(
                List.of("ADMIT_CARD_GENERATED", "TEST_COMPLETED"));

        long submittedCount = repository.countByStatus("SUBMITTED");
        long admitCardCount = repository.countByStatus("ADMIT_CARD_GENERATED");
        long testCompletedCount = repository.countByStatus("TEST_COMPLETED");
        long meritProcessedCount = repository.countByStatus("MERIT_PROCESSED");
        long allocatedCount = repository.countByStatus("ALLOCATED");

        double totalSsc = 0;
        double totalHsc = 0;
        int withTest = 0;
        for (PreAdmissionRegistration reg : eligible) {
            if (reg.getSscGpa() != null) totalSsc += reg.getSscGpa();
            if (reg.getHscGpa() != null) totalHsc += reg.getHscGpa();
            if (testResultRepository.findByRegistration_Id(reg.getId()).isPresent()) withTest++;
        }

        double avgSsc = eligible.isEmpty() ? 0 : totalSsc / eligible.size();
        double avgHsc = eligible.isEmpty() ? 0 : totalHsc / eligible.size();

        return Map.of(
                "totalEligible", eligible.size(),
                "withTestResults", withTest,
                "withoutTestResults", eligible.size() - withTest,
                "avgSscGpa", Math.round(avgSsc * 100.0) / 100.0,
                "avgHscGpa", Math.round(avgHsc * 100.0) / 100.0,
                "statusBreakdown", Map.of(
                        "SUBMITTED", submittedCount,
                        "ADMIT_CARD_GENERATED", admitCardCount,
                        "TEST_COMPLETED", testCompletedCount,
                        "MERIT_PROCESSED", meritProcessedCount,
                        "ALLOCATED", allocatedCount
                )
        );
    }
}
