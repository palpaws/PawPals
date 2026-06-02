package com.he186674.mvc.petshop.service;

import com.he186674.mvc.petshop.entities.PetReminder;

import java.time.LocalDate;
import java.util.List;

public interface PetReminderService {

    List<PetReminder> getRemindersByUserAndMonth(Integer userId, Integer petId, int year, int month);

    List<PetReminder> getUpcomingReminders(Integer userId, Integer petId);

    PetReminder createReminder(PetReminder reminder);

    PetReminder updateReminder(PetReminder reminder);

    void deleteReminder(Integer reminderId, Integer userId);

    void toggleComplete(Integer reminderId, Integer userId);

    PetReminder getReminderById(Integer reminderId);
}