package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByParentIsNullAndActiveTrueOrderByOrderNo();
    List<Menu> findByParentIsNullOrderByOrderNo();

    @Query("SELECT m FROM Menu m WHERE m.parent IS NULL AND m.active = true AND m.visible = true AND (m.permissionCode IS NULL OR m.permissionCode IN :permissions) ORDER BY m.orderNo")
    List<Menu> findAuthorizedMenus(@Param("permissions") List<String> permissions);

    List<Menu> findByModule(String module);
    Optional<Menu> findByTitleAndParent(String title, Menu parent);
}
