package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Optional<Program> findByCode(String code);
    boolean existsByCode(String code);
    List<Program> findByDepartment_Id(Long departmentId);
    List<Program> findByAdministrationDivision_Id(Long administrationDivisionId);
}
