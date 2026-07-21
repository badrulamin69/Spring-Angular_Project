package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.SemesterEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterEnrollmentRepository extends JpaRepository<SemesterEnrollment, Long> {

    Page<SemesterEnrollment> findByDeletedFalse(Pageable pageable);

    List<SemesterEnrollment> findByStudent_IdAndDeletedFalse(Long studentId);

    Optional<SemesterEnrollment> findByStudent_IdAndSemester_IdAndDeletedFalse(Long studentId, Long semesterId);

    boolean existsByStudent_IdAndSemester_IdAndDeletedFalse(Long studentId, Long semesterId);

    List<SemesterEnrollment> findBySemester_IdAndDeletedFalse(Long semesterId);

    Page<SemesterEnrollment> findBySemester_IdAndDeletedFalse(Long semesterId, Pageable pageable);

    List<SemesterEnrollment> findByStatusAndDeletedFalse(String status);

    List<SemesterEnrollment> findBySemester_IdAndStatusAndDeletedFalse(Long semesterId, String status);

    long countBySemester_IdAndDeletedFalse(Long semesterId);

    long countBySemester_IdAndStatusAndDeletedFalse(Long semesterId, String status);

    long countByDepartment_IdAndSemester_IdAndDeletedFalse(Long departmentId, Long semesterId);

    long countByFaculty_IdAndSemester_IdAndDeletedFalse(Long facultyId, Long semesterId);

    long countByProgram_IdAndSemester_IdAndDeletedFalse(Long programId, Long semesterId);

    @Query("SELECT se FROM SemesterEnrollment se WHERE se.deleted = false AND se.advisorStatus = 'PENDING' AND se.semester.id = :semesterId")
    List<SemesterEnrollment> findPendingAdvisorApprovals(@Param("semesterId") Long semesterId);

    @Query("SELECT se FROM SemesterEnrollment se WHERE se.deleted = false AND se.advisor.id = :advisorId AND se.advisorStatus = 'PENDING' AND se.semester.id = :semesterId")
    List<SemesterEnrollment> findPendingByAdvisor(@Param("advisorId") Long advisorId, @Param("semesterId") Long semesterId);

    @Query("SELECT se FROM SemesterEnrollment se WHERE se.deleted = false AND se.student.id = :studentId AND se.semester.id = :semesterId AND se.status IN ('APPROVED', 'COMPLETED')")
    Optional<SemesterEnrollment> findActiveEnrollment(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    @Query("SELECT se FROM SemesterEnrollment se WHERE se.deleted = false AND se.student.user.id = :userId")
    Optional<SemesterEnrollment> findByUserId(@Param("userId") Long userId);

    @Query("SELECT se FROM SemesterEnrollment se WHERE se.deleted = false AND se.enrollmentNumber = :enrollmentNumber")
    Optional<SemesterEnrollment> findByEnrollmentNumber(@Param("enrollmentNumber") String enrollmentNumber);

    @Query("SELECT COUNT(se) FROM SemesterEnrollment se WHERE se.deleted = false AND se.semester.id = :semesterId AND se.status <> 'CANCELLED'")
    long countEnrolledStudents(@Param("semesterId") Long semesterId);

    Page<SemesterEnrollment> findByDeletedFalseAndDepartment_Id(Long departmentId, Pageable pageable);

    Page<SemesterEnrollment> findByDeletedFalseAndFaculty_Id(Long facultyId, Pageable pageable);

    Page<SemesterEnrollment> findByDeletedFalseAndProgram_Id(Long programId, Pageable pageable);

    @Query("SELECT se FROM SemesterEnrollment se WHERE se.deleted = false AND (:semesterId IS NULL OR se.semester.id = :semesterId) AND (:departmentId IS NULL OR se.department.id = :departmentId) AND (:facultyId IS NULL OR se.faculty.id = :facultyId) AND (:programId IS NULL OR se.program.id = :programId) AND (:status IS NULL OR se.status = :status)")
    Page<SemesterEnrollment> findFiltered(@Param("semesterId") Long semesterId, @Param("departmentId") Long departmentId, @Param("facultyId") Long facultyId, @Param("programId") Long programId, @Param("status") String status, Pageable pageable);
}
