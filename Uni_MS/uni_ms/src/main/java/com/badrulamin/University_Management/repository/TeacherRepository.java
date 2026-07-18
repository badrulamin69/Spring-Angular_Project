package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    boolean existsByEmail(String email);

    boolean existsByTeacherCode(String teacherCode);

    @Query("SELECT t FROM Teacher t WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(t.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.teacherCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.designation) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:departmentId IS NULL OR t.department.id = :departmentId) AND " +
           "(:facultyId IS NULL OR t.faculty.id = :facultyId) AND " +
           "(:designation IS NULL OR :designation = '' OR LOWER(t.designation) = LOWER(:designation)) AND " +
           "(:status IS NULL OR :status = '' OR LOWER(t.status) = LOWER(:status))")
    Page<Teacher> searchTeachers(
            @Param("search") String search,
            @Param("departmentId") Long departmentId,
            @Param("facultyId") Long facultyId,
            @Param("designation") String designation,
            @Param("status") String status,
            Pageable pageable);
}
