package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionConfirmation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionConfirmationRepository extends JpaRepository<AdmissionConfirmation, Long> {

    Optional<AdmissionConfirmation> findByConfirmationNumber(String confirmationNumber);

    Optional<AdmissionConfirmation> findByAllocation_Id(Long allocationId);

    Optional<AdmissionConfirmation> findByRegistration_Id(Long registrationId);

    Page<AdmissionConfirmation> findByStatus(String status, Pageable pageable);

    long countByStatus(String status);

    long countByDocumentsVerified(Boolean documentsVerified);

    long countByFeePaid(Boolean feePaid);

    boolean existsByAllocation_Id(Long allocationId);

    boolean existsByRegistration_Id(Long registrationId);

    List<AdmissionConfirmation> findByRegistration_IdAndStatus(Long registrationId, String status);

    @Query("SELECT c FROM AdmissionConfirmation c WHERE " +
           "(:search IS NULL OR LOWER(c.confirmationNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.registration.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.registration.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.registration.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:documentsVerified IS NULL OR c.documentsVerified = :documentsVerified) " +
           "AND (:feePaid IS NULL OR c.feePaid = :feePaid)")
    Page<AdmissionConfirmation> findByFilters(@Param("search") String search,
                                               @Param("status") String status,
                                               @Param("documentsVerified") Boolean documentsVerified,
                                               @Param("feePaid") Boolean feePaid,
                                               Pageable pageable);
}
