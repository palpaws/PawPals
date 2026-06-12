package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // Lấy 10 thông báo mới nhất của user
    List<Notification> findTop10ByUser_UserIdOrderByCreatedAtDesc(Integer userId);

    // Đếm số lượng chưa đọc
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.userId = :userId AND (n.isRead IS NULL OR n.isRead = false)")
    long countUnreadByUserId(@Param("userId") Integer userId);

    // Đánh dấu một thông báo đã đọc
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notificationId = :id AND n.user.userId = :userId")
    int markAsRead(@Param("id") Integer id, @Param("userId") Integer userId);

    // Đánh dấu tất cả đã đọc
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.userId = :userId AND (n.isRead IS NULL OR n.isRead = false)")
    int markAllAsReadByUserId(@Param("userId") Integer userId);

    // Kiểm tra xem notification đã tồn tại với nội dung tương tự trong ngày chưa (tránh trùng lặp)
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.userId = :userId AND n.title = :title AND n.content = :content AND n.createdAt >= CURRENT_DATE")
    long countDuplicateToday(@Param("userId") Integer userId, @Param("title") String title, @Param("content") String content);

    // Kiểm tra xem đã có notification cho reminder này chưa
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.reminder.reminderId = :reminderId AND n.type = :type")
    long countByReminderIdAndType(@Param("reminderId") Integer reminderId, @Param("type") String type);

    // Đánh dấu tất cả notification liên quan đến reminder là đã đọc
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.reminder.reminderId = :reminderId")
    int markAsReadByReminderId(@Param("reminderId") Integer reminderId);
}
