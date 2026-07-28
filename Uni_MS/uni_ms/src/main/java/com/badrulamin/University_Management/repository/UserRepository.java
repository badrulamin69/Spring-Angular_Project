package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPasswordResetToken(String token);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String username, String email, String firstName, String lastName, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (:search IS NULL OR :search = '' OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    long countByActiveTrue();

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.code = :roleCode")
    long countByRoleCode(@Param("roleCode") String roleCode);

    long countByLockedUntilIsNotNullAndLockedUntilAfter(LocalDateTime now);

    @Query("SELECT COUNT(u) FROM User u WHERE u.lockedUntil IS NOT NULL AND u.lockedUntil > :now")
    long countLockedAccounts(@Param("now") LocalDateTime now);
}
