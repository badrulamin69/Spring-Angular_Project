package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.BookReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookReturnRepository extends JpaRepository<BookReturn, Long> {
}
