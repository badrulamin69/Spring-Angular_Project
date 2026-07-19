package com.badrulamin.University_Management.service;

import org.springframework.stereotype.Component;

@Component
public class NotificationHelper {

    private final NotificationService notificationService;

    public NotificationHelper(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void send(Long userId, String title, String message, String type, String module) {
        notificationService.createNotification(userId, title, message, type, module);
    }

    public void admitCardGenerated(Long userId, String testName, String admitCardNumber) {
        send(userId, "Admit Card Generated",
             "Your admit card for " + testName + " has been generated. Card No: " + admitCardNumber,
             "INFO", "ADMISSION_TEST");
    }

    public void testResultPublished(Long userId, String testName, double score, double percentage) {
        send(userId, "Test Result Published",
             "Your result for " + testName + " has been published. Score: " + score + " (" + String.format("%.1f", percentage) + "%)",
             "INFO", "ADMISSION_TEST");
    }

    public void meritListPublished(Long userId, String listName, int rank) {
        send(userId, "Merit List Published",
             "You have been ranked #" + rank + " in " + listName,
             "SUCCESS", "ADMISSION_TEST");
    }

    public void seatAllocated(Long userId, String testName, String seatInfo) {
        send(userId, "Seat Allocated",
             "Your seat for " + testName + " has been allocated: " + seatInfo,
             "INFO", "ADMISSION_TEST");
    }

    public void eligibilityVerified(Long userId, String testName, boolean eligible) {
        String status = eligible ? "eligible" : "not eligible";
        send(userId, "Eligibility Verified",
             "You have been verified as " + status + " for " + testName,
             eligible ? "SUCCESS" : "WARNING", "ADMISSION_TEST");
    }
}
