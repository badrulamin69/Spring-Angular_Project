package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Prerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrerequisiteRepository extends JpaRepository<Prerequisite, Long> {
    List<Prerequisite> findBySubject_Id(Long subjectId);
    List<Prerequisite> findByPrerequisiteSubject_Id(Long prerequisiteSubjectId);
}
