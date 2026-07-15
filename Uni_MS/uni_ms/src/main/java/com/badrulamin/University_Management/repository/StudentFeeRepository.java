package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.StudentFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentFeeRepository extends JpaRepository<StudentFee, Long> {
}
