package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.LoginSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoginSessionRepository extends JpaRepository<LoginSession, Long> {

    List<LoginSession> findByUser_IdOrderByLoginTimeDesc(Long userId);

    List<LoginSession> findByIsActiveTrue();

    List<LoginSession> findByIsActiveTrueAndUser_Id(Long userId);

    long countByIsActiveTrue();

    long countByUser_IdAndIsActiveTrue(Long userId);

    Optional<LoginSession> findBySessionToken(String sessionToken);

    void deleteByUser_Id(Long userId);

    List<LoginSession> findTop20ByOrderByLoginTimeDesc();
}
