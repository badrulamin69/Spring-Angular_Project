package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Student> searchStudents(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:departmentId IS NULL OR s.department.id = :departmentId)")
    Page<Student> findAllWithFilters(@Param("status") String status,
                                     @Param("departmentId") Long departmentId,
                                     Pageable pageable);

    @Query("SELECT s FROM Student s WHERE " +
           "(LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:departmentId IS NULL OR s.department.id = :departmentId)")
    Page<Student> searchStudentsWithFilters(@Param("keyword") String keyword,
                                            @Param("status") String status,
                                            @Param("departmentId") Long departmentId,
                                            Pageable pageable);
}
