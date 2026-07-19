package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmitCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmitCardRepository extends JpaRepository<AdmitCard, Long> {
    Optional<AdmitCard> findByAdmitCardNumber(String admitCardNumber);
    Optional<AdmitCard> findByTestIdAndRegistrationId(Long testId, Long registrationId);
    List<AdmitCard> findByTestId(Long testId);
    List<AdmitCard> findByRegistrationId(Long registrationId);
    boolean existsByAdmitCardNumber(String admitCardNumber);
    long countByTestId(Long testId);
    long countByTestIdAndStatus(Long testId, String status);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(ac.admitCardNumber, LENGTH(ac.admitCardNumber) - 5) AS long)), 0) FROM AdmitCard ac WHERE ac.test.id = :testId")
    Long findMaxAdmitCardSequence(@Param("testId") Long testId);
}
