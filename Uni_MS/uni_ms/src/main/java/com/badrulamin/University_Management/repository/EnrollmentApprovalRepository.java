package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.EnrollmentApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentApprovalRepository extends JpaRepository<EnrollmentApproval, Long> {

    List<EnrollmentApproval> findBySemesterEnrollment_IdOrderByCreatedAtDesc(Long semesterEnrollmentId);

    List<EnrollmentApproval> findByAdvisor_IdOrderByCreatedAtDesc(Long advisorId);
}
