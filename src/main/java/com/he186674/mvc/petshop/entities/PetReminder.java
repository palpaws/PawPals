package com.he186674.mvc.petshop.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_reminders")
public class PetReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reminder_id")
    private Integer reminderId;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "event_type", nullable = false)
    private String eventType; // KHAM, SPA, PHOI_GIONG, VACCINE, OTHER

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_time")
    private String eventTime;

    @Column(name = "location")
    private String location;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "is_recurring")
    private Boolean isRecurring = false;

    @Column(name = "recurring_interval_days")
    private Integer recurringIntervalDays;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===== GETTER & SETTER =====

    public Integer getReminderId() { return reminderId; }
    public void setReminderId(Integer reminderId) { this.reminderId = reminderId; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }

    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }

    public Integer getRecurringIntervalDays() { return recurringIntervalDays; }
    public void setRecurringIntervalDays(Integer recurringIntervalDays) { this.recurringIntervalDays = recurringIntervalDays; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}