package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:departmentId IS NULL OR c.department.id = :departmentId) AND " +
           "(:programId IS NULL OR c.program.id = :programId)")
    Page<Course> searchCourses(
            @Param("search") String search,
            @Param("departmentId") Long departmentId,
            @Param("programId") Long programId,
            Pageable pageable);
}
