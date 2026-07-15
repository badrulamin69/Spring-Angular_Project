package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);

    @Query("SELECT d FROM Department d WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:facultyId IS NULL OR d.faculty.id = :facultyId)")
    Page<Department> search(@Param("search") String search,
                            @Param("facultyId") Long facultyId,
                            Pageable pageable);
}
