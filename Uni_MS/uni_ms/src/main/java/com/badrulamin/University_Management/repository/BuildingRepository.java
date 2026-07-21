package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findByIsActiveTrue();
    Optional<Building> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByName(String name);

    @Query("SELECT b FROM Building b WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.code) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:isActive IS NULL OR b.isActive = :isActive)")
    List<Building> search(@Param("search") String search, @Param("isActive") Boolean isActive);
}
