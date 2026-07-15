package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.BookIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
}
