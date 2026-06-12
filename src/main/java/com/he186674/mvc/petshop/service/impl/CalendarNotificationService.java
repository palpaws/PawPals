package com.he186674.mvc.petshop.service.impl;

import com.he186674.mvc.petshop.entities.PetReminder;
import com.he186674.mvc.petshop.repository.NotificationRepository;
import com.he186674.mvc.petshop.repository.PetReminderRepository;
import com.he186674.mvc.petshop.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CalendarNotificationService {

    private final PetReminderRepository petReminderRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Autowired
    public CalendarNotificationService(PetReminderRepository petReminderRepository,
                                       NotificationService notificationService,
                                       NotificationRepository notificationRepository) {
        this.petReminderRepository = petReminderRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Tự động sinh notification cho một user dựa trên reminders của họ.
     * Gọi khi user đăng nhập hoặc render header.
     */
    public void generateReminderNotifications(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);

        // Lấy tất cả reminders chưa hoàn thành của user
        List<PetReminder> reminders = petReminderRepository
                .findByUser_UserIdAndIsCompletedFalseOrderByEventDateAsc(userId);

        for (PetReminder reminder : reminders) {
            if (reminder.getEventDate() == null) continue;

            String petName = reminder.getPet().getPetName();
            String eventLabel = getEventTypeLabel(reminder.getEventType());

            if (reminder.getEventDate().equals(today)) {
                // A. Reminder hôm nay
                createIfNotExist(reminder, "REMINDER_TODAY",
                        "Lịch nhắc hôm nay",
                        petName + " có lịch " + eventLabel + " hôm nay (" + today + ").");

            } else if (reminder.getEventDate().equals(tomorrow)) {
                // B. Reminder ngày mai
                createIfNotExist(reminder, "REMINDER_TOMORROW",
                        "Lịch nhắc sắp đến",
                        petName + " có lịch " + eventLabel + " vào ngày mai (" + tomorrow + ").");

            } else if (reminder.getEventDate().isBefore(today)) {
                // C. Reminder quá hạn
                createIfNotExist(reminder, "REMINDER_OVERDUE",
                        "Lịch nhắc quá hạn",
                        petName + " đã bỏ lỡ lịch " + eventLabel + " vào ngày " + reminder.getEventDate() + ".");
            }
        }
    }

    /**
     * Tạo notification nếu chưa tồn tại cho reminder + type này.
     */
    private void createIfNotExist(PetReminder reminder, String type, String title, String content) {
        long count = notificationRepository.countByReminderIdAndType(
                reminder.getReminderId(), type);
        if (count > 0) {
            return; // đã tồn tại, không tạo thêm
        }

        notificationService.createNotificationWithReminder(
                reminder.getUser().getUserId(),
                reminder.getReminderId(),
                type,
                title,
                content
        );
    }

    /**
     * Khi reminder được đánh dấu hoàn thành, tự động đánh dấu các notification
     * liên quan đến reminder đó là đã đọc.
     */
    public void markNotificationReadForReminder(Integer reminderId) {
        notificationRepository.markAsReadByReminderId(reminderId);
    }

    /**
     * Scheduled task: chạy mỗi 2 giờ để tạo notification cho tất cả user.
     * Chỉ xử lý reminders chưa hoàn thành.
     */
    @Scheduled(fixedRate = 7200000) // every 2 hours
    public void scheduledGenerateAll() {
        // Lấy tất cả reminders chưa hoàn thành, nhóm theo user
        List<PetReminder> allReminders = petReminderRepository
                .findByIsCompletedFalseOrderByUserUserId();

        // Track userId đã xử lý để tránh gọi nhiều lần
        allReminders.stream()
                .map(r -> r.getUser().getUserId())
                .distinct()
                .forEach(this::generateReminderNotifications);
    }

    private String getEventTypeLabel(String type) {
        if (type == null) return "sự kiện";
        return switch (type.toUpperCase()) {
            case "VACCINE" -> "tiêm vaccine";
            case "KHAM" -> "khám sức khỏe";
            case "SPA" -> "spa";
            case "PHOI_GIONG" -> "ghép đôi";
            default -> type.toLowerCase();
        };
    }
}