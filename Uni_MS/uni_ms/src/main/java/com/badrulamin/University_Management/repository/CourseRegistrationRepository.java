package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.CourseRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {
    Page<CourseRegistration> findByStudent_Id(Long studentId, Pageable pageable);
    List<CourseRegistration> findBySemester_Id(Long semesterId);
    long countByStatus(String status);
    long countByStudent_IdAndSemester_Id(Long studentId, Long semesterId);

    List<CourseRegistration> findByStudent_IdAndSemester_IdAndStatusIn(Long studentId, Long semesterId, List<String> statuses);

    Optional<CourseRegistration> findByStudent_IdAndSemester_IdAndCourse_Id(Long studentId, Long semesterId, Long courseId);

    @Query("SELECT COALESCE(SUM(cr.creditHours), 0) FROM CourseRegistration cr WHERE cr.student.id = :studentId AND cr.semester.id = :semesterId AND cr.status IN ('SELECTED', 'APPROVED', 'REGISTERED', 'PENDING')")
    Integer sumCreditHoursByStudentAndSemester(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    @Query("SELECT cr FROM CourseRegistration cr WHERE cr.advisorStatus = 'PENDING' AND cr.semester.id = :semesterId")
    List<CourseRegistration> findPendingAdvisorApprovals(@Param("semesterId") Long semesterId);

    @Query("SELECT cr FROM CourseRegistration cr WHERE cr.student.id = :studentId AND cr.semester.id = :semesterId AND cr.status = 'REGISTERED'")
    List<CourseRegistration> findRegisteredCourses(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    @Query("SELECT cr FROM CourseRegistration cr WHERE cr.student.id = :studentId AND cr.semester.id = :semesterId AND cr.advisorStatus = 'APPROVED' AND cr.paymentStatus != 'PAID'")
    List<CourseRegistration> findApprovedUnpaidCourses(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    boolean existsByStudent_IdAndSemester_IdAndCourse_IdAndStatusIn(Long studentId, Long semesterId, Long courseId, List<String> statuses);

    long countBySemester_Id(Long semesterId);

    @Query("SELECT COUNT(cr) FROM CourseRegistration cr WHERE cr.semester.id = :semesterId AND cr.advisorStatus = :status")
    long countBySemesterIdAndAdvisorStatus(@Param("semesterId") Long semesterId, @Param("status") String status);

    @Query("SELECT COUNT(cr) FROM CourseRegistration cr WHERE cr.semester.id = :semesterId AND cr.status = :status")
    long countBySemesterIdAndStatus(@Param("semesterId") Long semesterId, @Param("status") String status);

    @Query("SELECT cr.status, COUNT(cr) FROM CourseRegistration cr WHERE cr.semester.id = :semesterId GROUP BY cr.status")
    List<Object[]> countGroupByStatus(@Param("semesterId") Long semesterId);

    List<CourseRegistration> findBySemester_IdOrderByCreatedAtDesc(Long semesterId, Pageable pageable);
}
