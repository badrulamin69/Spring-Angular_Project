package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.QuickAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuickActionRepository extends JpaRepository<QuickAction, Long> {
    List<QuickAction> findByRole_IdAndVisibleTrueOrderByOrderNo(Long roleId);
}
