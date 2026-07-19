package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdmissionTestRepository extends JpaRepository<AdmissionTest, Long> {

    @Query("SELECT t FROM AdmissionTest t WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR :status = '' OR t.status = :status) " +
           "AND (:facultyId IS NULL OR t.faculty.id = :facultyId) " +
           "AND (:departmentId IS NULL OR t.department.id = :departmentId) " +
           "AND (:testDate IS NULL OR t.testDate = :testDate)")
    Page<AdmissionTest> findByFilters(@Param("search") String search,
                                       @Param("status") String status,
                                       @Param("facultyId") Long facultyId,
                                       @Param("departmentId") Long departmentId,
                                       @Param("testDate") LocalDate testDate,
                                       Pageable pageable);

    List<AdmissionTest> findByStatus(String status);
    List<AdmissionTest> findByTestDate(LocalDate testDate);
    long countByStatus(String status);
    long countByFacultyId(Long facultyId);
    long countByDepartmentId(Long departmentId);
}
