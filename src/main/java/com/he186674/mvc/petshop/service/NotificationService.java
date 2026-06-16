package com.he186674.mvc.petshop.service;

import com.he186674.mvc.petshop.entities.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getNotificationsByUser(Integer userId);

    long getUnreadCount(Integer userId);

    void markAsRead(Integer notificationId, Integer userId);

    void markAllAsRead(Integer userId);

    Notification createNotification(Integer userId, String title, String content);

    Notification createNotificationWithReminder(Integer userId, Integer reminderId, String type, String title, String content);
}
