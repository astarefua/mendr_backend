package com.telemed.service;

import com.telemed.model.Notification;
import com.telemed.repository.NotificationRepository;
import com.telemed.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    public void sendNotification(String message, String recipientEmail) {
        Notification notification = new Notification(message, recipientEmail);
        notificationRepo.save(notification);
    }

    public List<Notification> getMyNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        return notificationRepo.findByRecipientEmailOrderByTimestampDesc(email);
    }

    public void markAsRead(Long notificationId) {
        notificationRepo.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepo.save(n);
        });
    }
}
