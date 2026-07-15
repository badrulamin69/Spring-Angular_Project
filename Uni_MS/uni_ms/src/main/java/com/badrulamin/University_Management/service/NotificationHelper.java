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
}
