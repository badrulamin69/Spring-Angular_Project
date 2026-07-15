package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {
    Optional<Feature> findByFeatureKey(String featureKey);
    boolean existsByFeatureKey(String featureKey);
    List<Feature> findByModuleNameOrderBySortOrderAsc(String moduleName);
    List<Feature> findAllByOrderByModuleNameAscSortOrderAsc();
    List<Feature> findByIsEnabledTrue();
    List<Feature> findByCategoryOrderByModuleNameAscSortOrderAsc(String category);
    List<Feature> findByModuleNameAndCategoryOrderBySortOrderAsc(String moduleName, String category);

    @Query("SELECT DISTINCT f.moduleName FROM Feature f ORDER BY f.moduleName")
    List<String> findDistinctModules();

    @Query("SELECT DISTINCT f.category FROM Feature f ORDER BY f.category")
    List<String> findDistinctCategories();

    @Query("SELECT f.featureKey FROM Feature f WHERE f.isEnabled = true")
    List<String> findAllEnabledFeatureKeys();
}
