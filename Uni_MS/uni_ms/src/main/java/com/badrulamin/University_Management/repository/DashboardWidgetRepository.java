package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.DashboardWidget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DashboardWidgetRepository extends JpaRepository<DashboardWidget, Long> {
//    List<DashboardWidget> findByRoleIdAndVisibleTrueOrderByOrderNo(Long roleId);

    List<DashboardWidget> findByRole_IdAndVisibleTrueOrderByOrderNo(Long roleId);

}
