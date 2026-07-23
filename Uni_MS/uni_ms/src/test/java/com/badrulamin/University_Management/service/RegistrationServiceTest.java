package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.request.CourseRegistrationRequest;
import com.badrulamin.University_Management.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private CourseRegistrationRepository courseRegistrationRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private SubjectOfferingRepository subjectOfferingRepository;

    @Mock
    private RegistrationConfigService registrationConfigService;

    @Mock
    private ValidationService validationService;

    @Mock
    private EligibilityService eligibilityService;

    @Mock
    private RegistrationHistoryService historyService;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void getRegistrationById_existingRegistration_returnsRegistration() {
        CourseRegistration reg = new CourseRegistration();
        reg.setId(1L);
        reg.setStatus("SELECTED");

        when(courseRegistrationRepository.findById(1L)).thenReturn(Optional.of(reg));

        CourseRegistration result = registrationService.getRegistrationById(1L);

        assertEquals("SELECTED", result.getStatus());
        verify(courseRegistrationRepository).findById(1L);
    }

    @Test
    void getRegistrationById_nonExisting_throwsException() {
        when(courseRegistrationRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> registrationService.getRegistrationById(999L));

        assertTrue(ex.getMessage().contains("CourseRegistration"));
    }

    @Test
    void getStudentRegistrations_returnsList() {
        Student student = new Student();
        student.setId(1L);
        Semester semester = new Semester();
        semester.setId(1L);

        CourseRegistration reg = CourseRegistration.builder()
                .student(student)
                .semester(semester)
                .status("REGISTERED")
                .build();

        List<String> expectedStatuses = List.of("SELECTED", "PENDING", "APPROVED", "REGISTERED", "DROPPED");
        when(courseRegistrationRepository.findByStudent_IdAndSemester_IdAndStatusIn(1L, 1L, expectedStatuses))
                .thenReturn(List.of(reg));

        List<CourseRegistration> result = registrationService.getStudentRegistrations(1L, 1L);

        assertEquals(1, result.size());
        assertEquals("REGISTERED", result.get(0).getStatus());
    }

    @Test
    void finalizeRegistration_notApproved_throwsException() {
        CourseRegistration reg = CourseRegistration.builder()
                .advisorStatus("PENDING")
                .build();
        reg.setId(1L);

        when(courseRegistrationRepository.findById(1L)).thenReturn(Optional.of(reg));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> registrationService.finalizeRegistration(1L, 1L));

        assertTrue(ex.getMessage().contains("approved by advisor"));
    }

    @Test
    void finalizeRegistration_approvedButNotPaid_throwsException() {
        Semester semester = new Semester();
        semester.setId(1L);

        CourseRegistration reg = CourseRegistration.builder()
                .advisorStatus("APPROVED")
                .paymentStatus("PENDING")
                .semester(semester)
                .build();
        reg.setId(1L);

        RegistrationConfig config = RegistrationConfig.builder()
                .paymentRequired(true)
                .semester(semester)
                .build();

        when(courseRegistrationRepository.findById(1L)).thenReturn(Optional.of(reg));
        when(registrationConfigService.getActiveConfigOrThrow(1L)).thenReturn(config);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> registrationService.finalizeRegistration(1L, 1L));

        assertTrue(ex.getMessage().contains("Payment must be completed"));
    }

    @Test
    void dropCourse_addDropNotAllowed_throwsException() {
        Semester semester = new Semester();
        semester.setId(1L);

        CourseRegistration reg = CourseRegistration.builder()
                .semester(semester)
                .status("SELECTED")
                .build();
        reg.setId(1L);

        RegistrationConfig config = RegistrationConfig.builder()
                .allowAddDrop(false)
                .semester(semester)
                .build();

        when(courseRegistrationRepository.findById(1L)).thenReturn(Optional.of(reg));
        when(registrationConfigService.getActiveConfigOrThrow(1L)).thenReturn(config);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> registrationService.dropCourse(1L, 1L));

        assertTrue(ex.getMessage().contains("not allowed"));
    }

    @Test
    void dropCourse_registrationNotFound_throwsException() {
        when(courseRegistrationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> registrationService.dropCourse(999L, 1L));
    }

    @Test
    void selectCourse_validationFails_throwsBusinessException() {
        Student student = new Student();
        student.setId(1L);

        Subject subject = new Subject();
        subject.setId(1L);
        subject.setName("Math 101");
        subject.setCode("MATH101");
        subject.setCredits(3);

        Semester semester = new Semester();
        semester.setId(1L);

        CourseRegistrationRequest request = new CourseRegistrationRequest();
        request.setStudentId(1L);
        request.setSubjectId(1L);
        request.setSemesterId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(semester));
        when(courseRegistrationRepository.sumCreditHoursByStudentAndSemester(1L, 1L)).thenReturn(0);
        when(validationService.validateRegistration(1L, 1L, 1L, null, null))
                .thenReturn(List.of("Already registered for this course in the current semester"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> registrationService.selectCourse(request, 1L));

        assertTrue(ex.getMessage().contains("Already registered"));
        verify(courseRegistrationRepository, never()).save(any());
    }
}
