package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findByBuilding_Id(Long buildingId);
    List<Classroom> findByBuilding_IdAndIsActiveTrue(Long buildingId);
    List<Classroom> findByRoomType(String roomType);
    List<Classroom> findByIsLabTrue();
    List<Classroom> findByIsSmartClassroomTrue();
    List<Classroom> findByIsAvailableTrueAndIsActiveTrue();
    List<Classroom> findByCapacityGreaterThanEqual(Integer capacity);
    Optional<Classroom> findByBuilding_IdAndRoomNumber(Long buildingId, String roomNumber);
    boolean existsByBuilding_IdAndRoomNumber(Long buildingId, String roomNumber);

    @Query("SELECT c FROM Classroom c JOIN c.building b WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(c.roomNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:buildingId IS NULL OR c.building.id = :buildingId) " +
           "AND (:roomType IS NULL OR c.roomType = :roomType) " +
           "AND (:isLab IS NULL OR c.isLab = :isLab) " +
           "AND (:isSmartClassroom IS NULL OR c.isSmartClassroom = :isSmartClassroom) " +
           "AND (:isActive IS NULL OR c.isActive = :isActive)")
    List<Classroom> search(@Param("search") String search,
                           @Param("buildingId") Long buildingId,
                           @Param("roomType") String roomType,
                           @Param("isLab") Boolean isLab,
                           @Param("isSmartClassroom") Boolean isSmartClassroom,
                           @Param("isActive") Boolean isActive);
}
