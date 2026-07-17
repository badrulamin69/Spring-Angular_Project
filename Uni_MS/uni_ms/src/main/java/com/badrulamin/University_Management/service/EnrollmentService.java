package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final StudentRepository studentRepository;
    private final DepartmentAllocationRepository allocationRepository;
    private final PreAdmissionRegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final StudentIdGenerationRepository studentIdGenerationRepository;
    private final AdmissionEnrollmentRepository admissionEnrollmentRepository;

    private static final AtomicLong studentCounter = new AtomicLong(1);

    @Transactional
    public Map<String, Object> enrollFromAllocation(Long allocationId) {
        DepartmentAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("DepartmentAllocation", "id", allocationId));

        if (!"CONFIRMED".equals(allocation.getStatus())) {
            throw new IllegalArgumentException("Allocation must be CONFIRMED before enrollment. Current status: " + allocation.getStatus());
        }

        PreAdmissionRegistration reg = allocation.getRegistration();
        if (reg == null) {
            throw new IllegalArgumentException("No registration linked to this allocation");
        }

        if ("ENROLLED".equals(reg.getStatus())) {
            throw new IllegalArgumentException("This applicant is already enrolled. Registration: " + reg.getRegistrationNumber());
        }

        if (reg.getEmail() != null && studentRepository.existsByEmail(reg.getEmail())) {
            throw new IllegalArgumentException("A student with email " + reg.getEmail() + " already exists");
        }

        String studentCode = generateStudentCode();

        User foundUser = null;
        if (reg.getEmail() != null) {
            foundUser = userRepository.findByEmail(reg.getEmail()).orElse(null);
            if (foundUser == null) {
                foundUser = userRepository.findByUsername(reg.getEmail()).orElse(null);
            }
        }

        Student student = new Student();
        student.setFirstName(reg.getFirstName());
        student.setLastName(reg.getLastName());
        student.setEmail(reg.getEmail());
        student.setPhone(reg.getPhone());
        student.setDateOfBirth(reg.getDateOfBirth());
        student.setGender(reg.getGender());
        student.setStudentCode(studentCode);
        student.setEnrollmentDate(LocalDate.now());
        student.setStatus("ACTIVE");

        if (allocation.getAllocatedDepartment() != null) {
            student.setDepartment(allocation.getAllocatedDepartment());
        }

        if (foundUser != null) {
            final User user = foundUser;
            student.setUser(user);
            user.setFirstName(reg.getFirstName());
            user.setLastName(reg.getLastName());
            userRepository.save(user);
            roleRepository.findByCode("ROLE_STUDENT").ifPresent(role -> user.addRole(role));
            userRepository.save(user);
        }

        Student savedStudent = studentRepository.save(student);

        Batch batch = allocation.getAllocatedBatch();
        Section section = allocation.getAllocatedSection();

        AdmissionEnrollment enrollment = AdmissionEnrollment.builder()
                .enrollmentNumber("ENR-" + Year.now().getValue() + "-" + String.format("%05d", studentCounter.getAndIncrement()))
                .student(savedStudent)
                .program(allocation.getAllocatedProgram())
                .batch(batch)
                .section(section)
                .semester(allocation.getSemester())
                .status("ENROLLED")
                .enrolledAt(LocalDateTime.now())
                .isDocumentVerified(false)
                .isFeePaid(false)
                .totalFeePaid(0.0)
                .build();
        admissionEnrollmentRepository.save(enrollment);

        if (batch != null) {
            StudentEnrollment se = new StudentEnrollment();
            se.setStudent(savedStudent);
            se.setBatch(batch);
            se.setSection(section);
            se.setEnrollmentDate(LocalDate.now());
            se.setStatus("ACTIVE");
            studentEnrollmentRepository.save(se);
        }

        String idCardNumber = "ID-" + studentCode.replace("STU-", "");
        StudentIdGeneration idGen = StudentIdGeneration.builder()
                .studentId(studentCode)
                .studentName(reg.getFirstName() + " " + reg.getLastName())
                .department(allocation.getAllocatedDepartment() != null ? allocation.getAllocatedDepartment().getName() : null)
                .program(allocation.getAllocatedProgram() != null ? allocation.getAllocatedProgram().getName() : null)
                .batch(batch != null ? batch.getName() : null)
                .status("ACTIVE")
                .idCardNumber(idCardNumber)
                .student(savedStudent)
                .build();
        studentIdGenerationRepository.save(idGen);

        reg.setStatus("ENROLLED");
        registrationRepository.save(reg);

        allocation.setStatus("ENROLLED");
        allocationRepository.save(allocation);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Enrollment completed successfully");
        result.put("studentCode", studentCode);
        result.put("studentName", reg.getFirstName() + " " + reg.getLastName());
        result.put("enrollmentNumber", enrollment.getEnrollmentNumber());
        result.put("idCardNumber", idCardNumber);
        result.put("department", allocation.getAllocatedDepartment() != null ? allocation.getAllocatedDepartment().getName() : null);
        result.put("program", allocation.getAllocatedProgram() != null ? allocation.getAllocatedProgram().getName() : null);
        result.put("batch", batch != null ? batch.getName() : null);
        result.put("section", section != null ? section.getName() : null);
        result.put("studentId", savedStudent.getId());
        return result;
    }

    @Transactional
    public Map<String, Object> enrollSelf(Long registrationId, String userEmail) {
        PreAdmissionRegistration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("PreAdmissionRegistration", "id", registrationId));

        if (!reg.getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("You can only enroll your own application");
        }

        Optional<DepartmentAllocation> allocOpt = allocationRepository.findByRegistration_Id(registrationId);
        if (allocOpt.isEmpty()) {
            throw new IllegalArgumentException("No department allocation found for this registration");
        }

        DepartmentAllocation allocation = allocOpt.get();
        if (!"CONFIRMED".equals(allocation.getStatus())) {
            throw new IllegalArgumentException("Your allocation has not been confirmed yet. Current status: " + allocation.getStatus());
        }

        return enrollFromAllocation(allocation.getId());
    }

    public Page<DepartmentAllocation> findConfirmedNotEnrolled(Pageable pageable) {
        return allocationRepository.findByStatus("CONFIRMED", pageable);
    }

    public Map<String, Object> getEnrollmentStats() {
        long totalStudents = studentRepository.count();
        return Map.of("totalStudents", totalStudents);
    }

    private String generateStudentCode() {
        long count = studentRepository.count() + 1;
        return "STU-" + Year.now().getValue() + "-" + String.format("%05d", count);
    }
}
