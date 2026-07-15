package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Faculty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByName(String name);

    @Query("SELECT f FROM Faculty f WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(f.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(f.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:isActive IS NULL OR f.isActive = :isActive)")
    Page<Faculty> search(@Param("search") String search,
                         @Param("isActive") Boolean isActive,
                         Pageable pageable);
}
