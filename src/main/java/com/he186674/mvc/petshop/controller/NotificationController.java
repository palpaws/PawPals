package com.he186674.mvc.petshop.controller;

import com.he186674.mvc.petshop.entities.Notification;
import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.service.NotificationService;
import com.he186674.mvc.petshop.service.impl.CalendarNotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final CalendarNotificationService calendarNotificationService;

    @Autowired
    public NotificationController(NotificationService notificationService,
                                  CalendarNotificationService calendarNotificationService) {
        this.notificationService = notificationService;
        this.calendarNotificationService = calendarNotificationService;
    }

    @GetMapping("/notifications")
    public String notificationsPage() {
        return "Notifications";
    }

    // GET /api/notifications - Lấy danh sách thông báo
    @GetMapping("/api/notifications")
    @ResponseBody
    public ResponseEntity<?> getNotifications(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        List<Notification> notifications = notificationService.getNotificationsByUser(currentUser.getUserId());
        List<Map<String, Object>> dtos = notifications.stream().map(n -> {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("notificationId", n.getNotificationId());
            dto.put("title", n.getTitle());
            dto.put("content", n.getContent());
            dto.put("isRead", n.getIsRead());
            dto.put("createdAt", n.getCreatedAt() != null ? n.getCreatedAt().toString() : null);
            // Format time ago
            if (n.getCreatedAt() != null) {
                dto.put("timeAgo", timeAgo(n.getCreatedAt()));
            }
            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    // GET /api/notifications/unread-count - Số lượng chưa đọc
    @GetMapping("/api/notifications/unread-count")
    @ResponseBody
    public ResponseEntity<?> getUnreadCount(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.ok(Map.of("count", 0));
        }

        long count = notificationService.getUnreadCount(currentUser.getUserId());
        return ResponseEntity.ok(Map.of("count", count));
    }

    // PUT /api/notifications/{id}/read - Đánh dấu đã đọc
    @PutMapping("/api/notifications/{id}/read")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@PathVariable Integer id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        notificationService.markAsRead(id, currentUser.getUserId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    // POST /api/notifications/generate - Tự động sinh notification từ reminders (gọi từ frontend khi load trang)
    @PostMapping("/api/notifications/generate")
    @ResponseBody
    public ResponseEntity<?> generateNotifications(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        calendarNotificationService.generateReminderNotifications(currentUser.getUserId());
        long count = notificationService.getUnreadCount(currentUser.getUserId());
        return ResponseEntity.ok(Map.of("success", true, "unreadCount", count));
    }

    // PUT /api/notifications/read-all - Đánh dấu tất cả đã đọc
    @PutMapping("/api/notifications/read-all")
    @ResponseBody
    public ResponseEntity<?> markAllAsRead(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        notificationService.markAllAsRead(currentUser.getUserId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    // Helper: format thời gian
    private String timeAgo(java.time.LocalDateTime createdAt) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(createdAt, now);

        long minutes = duration.toMinutes();
        if (minutes < 1) return "Vừa xong";
        if (minutes < 60) return minutes + " phút trước";

        long hours = duration.toHours();
        if (hours < 24) return hours + " giờ trước";

        long days = duration.toDays();
        if (days < 7) return days + " ngày trước";

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return createdAt.format(formatter);
    }
}