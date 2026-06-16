package com.he186674.mvc.petshop.service.impl;

import com.he186674.mvc.petshop.entities.Notification;
import com.he186674.mvc.petshop.entities.PetReminder;
import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.repository.NotificationRepository;
import com.he186674.mvc.petshop.repository.PetReminderRepository;
import com.he186674.mvc.petshop.repository.UserRepository;
import com.he186674.mvc.petshop.service.NotificationService;
import com.he186674.mvc.petshop.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PetReminderRepository petReminderRepository;
    private final EmailService emailService;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   PetReminderRepository petReminderRepository,
                                   EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.petReminderRepository = petReminderRepository;
        this.emailService = emailService;
    }

    @Override
    public List<Notification> getNotificationsByUser(Integer userId) {
        return notificationRepository.findTop10ByUser_UserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public long getUnreadCount(Integer userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(Integer notificationId, Integer userId) {
        notificationRepository.markAsRead(notificationId, userId);
    }

    @Override
    public void markAllAsRead(Integer userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    public Notification createNotification(Integer userId, String title, String content) {
        return createNotificationWithReminder(userId, null, "SYSTEM", title, content);
    }

    @Override
    public Notification createNotificationWithReminder(Integer userId, Integer reminderId, String type, String title, String content) {
        // Check duplicate in same day
        long dupCount = notificationRepository.countDuplicateToday(userId, title, content);
        if (dupCount > 0) {
            return null; // skip duplicate
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type != null ? type : "SYSTEM");
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        // Link to reminder if provided
        if (reminderId != null) {
            PetReminder reminder = petReminderRepository.findById(reminderId).orElse(null);
            notification.setReminder(reminder);
        }

        Notification saved = notificationRepository.save(notification);

        // Gửi email thông báo cho user (bất đồng bộ, không blocking)
        try {
            emailService.sendNotificationEmail(
                    saved.getUser().getEmail(),
                    saved.getTitle(),
                    saved.getContent()
            );
        } catch (Exception e) {
            // Không làm gián đoạn luồng chính nếu email lỗi
            System.err.println("Email notification failed: " + e.getMessage());
        }

        return saved;
    }
}
