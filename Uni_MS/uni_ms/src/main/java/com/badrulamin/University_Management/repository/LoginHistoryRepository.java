package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.LoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    Page<LoginHistory> findByUserId(Long userId, Pageable pageable);
    List<LoginHistory> findTop10ByOrderByLoginTimestampDesc();
    long countBySuccessTrue();
    long countBySuccessFalse();

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.user.id = :userId AND lh.success = true ORDER BY lh.loginTimestamp DESC")
    List<LoginHistory> findLastLoginsByUserId(@Param("userId") Long userId, Pageable pageable);

    long countByLoginTimestampAfter(LocalDateTime date);

    Optional<LoginHistory> findTopByUser_IdAndLogoutTimestampIsNullOrderByLoginTimestampDesc(Long userId);
}
