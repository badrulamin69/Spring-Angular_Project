package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionMeritList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionMeritListRepository extends JpaRepository<AdmissionMeritList, Long> {
    Page<AdmissionMeritList> findByStatus(String status, Pageable pageable);
    List<AdmissionMeritList> findBySession_Id(Long sessionId);
    List<AdmissionMeritList> findByProgram_Id(Long programId);
    List<AdmissionMeritList> findByFaculty_Id(Long facultyId);
    List<AdmissionMeritList> findByDepartment_Id(Long departmentId);
    List<AdmissionMeritList> findByTestId(Long testId);
    long countByStatus(String status);
    long countBySession_Id(Long sessionId);

    @Query("SELECT m FROM AdmissionMeritList m WHERE " +
           "(:search IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR m.status = :status) " +
           "AND (:sessionId IS NULL OR m.session.id = :sessionId) " +
           "AND (:facultyId IS NULL OR m.faculty.id = :facultyId) " +
           "AND (:departmentId IS NULL OR m.department.id = :departmentId) " +
           "AND (:programId IS NULL OR m.program.id = :programId) " +
           "AND (:testId IS NULL OR m.test.id = :testId)")
    Page<AdmissionMeritList> findByFilters(
            @Param("search") String search,
            @Param("status") String status,
            @Param("sessionId") Long sessionId,
            @Param("facultyId") Long facultyId,
            @Param("departmentId") Long departmentId,
            @Param("programId") Long programId,
            @Param("testId") Long testId,
            Pageable pageable);
}
