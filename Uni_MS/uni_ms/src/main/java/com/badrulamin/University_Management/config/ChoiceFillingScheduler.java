package com.badrulamin.University_Management.config;

import com.badrulamin.University_Management.entity.ApplicantChoiceSubmission;
import com.badrulamin.University_Management.entity.ChoiceFillingConfig;
import com.badrulamin.University_Management.repository.ApplicantChoiceSubmissionRepository;
import com.badrulamin.University_Management.repository.ChoiceFillingConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChoiceFillingScheduler {

    private final ChoiceFillingConfigRepository configRepository;
    private final ApplicantChoiceSubmissionRepository submissionRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoLockExpiredSubmissions() {
        List<ChoiceFillingConfig> expiredConfigs = configRepository.findExpiredConfigsForAutoLock(LocalDateTime.now());
        for (ChoiceFillingConfig config : expiredConfigs) {
            List<ApplicantChoiceSubmission> submitted = submissionRepository
                    .findByConfig_IdAndStatus(config.getId(), "SUBMITTED");
            for (ApplicantChoiceSubmission submission : submitted) {
                submission.setStatus("LOCKED");
                submission.setLockedAt(LocalDateTime.now());
                submissionRepository.save(submission);
                log.info("Auto-locked submission {} for config {}", submission.getSubmissionId(), config.getId());
            }
            config.setStatus("CLOSED");
            configRepository.save(config);
            log.info("Auto-closed choice filling config {}", config.getId());
        }
    }
}
