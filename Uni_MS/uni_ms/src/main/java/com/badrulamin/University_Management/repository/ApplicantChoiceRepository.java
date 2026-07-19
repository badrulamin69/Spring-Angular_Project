package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.ApplicantChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantChoiceRepository extends JpaRepository<ApplicantChoice, Long> {

    List<ApplicantChoice> findBySubmission_IdOrderByPriorityAsc(Long submissionId);

    Optional<ApplicantChoice> findBySubmission_IdAndProgram_Id(Long submissionId, Long programId);

    boolean existsBySubmission_IdAndProgram_Id(Long submissionId, Long programId);

    long countBySubmission_Id(Long submissionId);

    void deleteBySubmission_Id(Long submissionId);

    @Query("SELECT c FROM ApplicantChoice c WHERE c.submission.config.id = :configId " +
           "AND c.program.id = :programId ORDER BY c.priority ASC")
    List<ApplicantChoice> findByConfigAndProgram(@Param("configId") Long configId, @Param("programId") Long programId);

    @Query("SELECT COUNT(c) FROM ApplicantChoice c WHERE c.program.id = :programId " +
           "AND c.submission.config.id = :configId AND c.submission.status IN :statuses")
    long countByProgramAndConfigAndStatuses(@Param("programId") Long programId,
                                            @Param("configId") Long configId,
                                            @Param("statuses") List<String> statuses);
}
