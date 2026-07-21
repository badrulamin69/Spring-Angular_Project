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
    Optional<AdmitCard> findByTest_IdAndRegistration_Id(Long testId, Long registrationId);
    List<AdmitCard> findByTest_Id(Long testId);
    List<AdmitCard> findByRegistration_Id(Long registrationId);
    boolean existsByAdmitCardNumber(String admitCardNumber);
    long countByTest_Id(Long testId);
    long countByTest_IdAndStatus(Long testId, String status);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(ac.admitCardNumber, LENGTH(ac.admitCardNumber) - 5) AS long)), 0) FROM AdmitCard ac WHERE ac.test.id = :testId")
    Long findMaxAdmitCardSequence(@Param("testId") Long testId);
}
