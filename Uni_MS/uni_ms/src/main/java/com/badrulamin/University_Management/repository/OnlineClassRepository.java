package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.OnlineClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnlineClassRepository extends JpaRepository<OnlineClass, Long> {
}
