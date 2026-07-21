package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findBySlotType(String slotType);
    List<TimeSlot> findByIsActiveTrue();
    List<TimeSlot> findByIsActiveTrueOrderBySortOrderAsc();
    List<TimeSlot> findBySortOrderBetween(Integer start, Integer end);
    Optional<TimeSlot> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByName(String name);
}
