package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.phone) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:departmentId IS NULL OR e.department.id = :departmentId) AND " +
           "(:designation IS NULL OR :designation = '' OR LOWER(e.designation) = LOWER(:designation)) AND " +
           "(:status IS NULL OR :status = '' OR LOWER(e.status) = LOWER(:status))")
    Page<Employee> searchEmployees(
            @Param("search") String search,
            @Param("departmentId") Long departmentId,
            @Param("designation") String designation,
            @Param("status") String status,
            Pageable pageable);
}
