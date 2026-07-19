package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionWaitingList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionWaitingListRepository extends JpaRepository<AdmissionWaitingList, Long> {
    Page<AdmissionWaitingList> findByStatus(String status, Pageable pageable);
    List<AdmissionWaitingList> findBySession_Id(Long sessionId);
    List<AdmissionWaitingList> findByProgram_Id(Long programId);
    List<AdmissionWaitingList> findByFaculty_Id(Long facultyId);
    List<AdmissionWaitingList> findByDepartment_Id(Long departmentId);
    List<AdmissionWaitingList> findByTestId(Long testId);
    long countByStatus(String status);

    @Query("SELECT w FROM AdmissionWaitingList w WHERE " +
           "(:search IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR w.status = :status) " +
           "AND (:sessionId IS NULL OR w.session.id = :sessionId) " +
           "AND (:facultyId IS NULL OR w.faculty.id = :facultyId) " +
           "AND (:programId IS NULL OR w.program.id = :programId) " +
           "AND (:testId IS NULL OR w.test.id = :testId)")
    Page<AdmissionWaitingList> findByFilters(
            @Param("search") String search,
            @Param("status") String status,
            @Param("sessionId") Long sessionId,
            @Param("facultyId") Long facultyId,
            @Param("programId") Long programId,
            @Param("testId") Long testId,
            Pageable pageable);
}
