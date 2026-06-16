package com.he186674.mvc.petshop.service.impl;

import com.he186674.mvc.petshop.entities.Pet;
import com.he186674.mvc.petshop.entities.PetReminder;
import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.repository.PetReminderRepository;
import com.he186674.mvc.petshop.repository.PetRepository;
import com.he186674.mvc.petshop.repository.UserRepository;
import com.he186674.mvc.petshop.service.PetReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class PetReminderServiceImpl implements PetReminderService {

    private final PetReminderRepository petReminderRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Autowired
    public PetReminderServiceImpl(PetReminderRepository petReminderRepository,
                                   PetRepository petRepository,
                                   UserRepository userRepository) {
        this.petReminderRepository = petReminderRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PetReminder> getRemindersByUserAndMonth(Integer userId, Integer petId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return petReminderRepository.findByUserAndDateRange(userId, startDate, endDate, petId);
    }

    @Override
    public List<PetReminder> getUpcomingReminders(Integer userId, Integer petId) {
        if (petId != null) {
            return petReminderRepository.findByUser_UserIdAndPet_PetIdAndIsCompletedFalseOrderByEventDateAsc(userId, petId);
        }
        return petReminderRepository.findByUser_UserIdAndIsCompletedFalseOrderByEventDateAsc(userId);
    }

    @Override
    public PetReminder createReminder(PetReminder reminder) {
        reminder.setCreatedAt(LocalDateTime.now());
        if (reminder.getIsCompleted() == null) {
            reminder.setIsCompleted(false);
        }
        if (reminder.getIsRecurring() == null) {
            reminder.setIsRecurring(false);
        }
        return petReminderRepository.save(reminder);
    }

    @Override
    public PetReminder updateReminder(PetReminder reminder) {
        return petReminderRepository.save(reminder);
    }

    @Override
    public void deleteReminder(Integer reminderId, Integer userId) {
        PetReminder reminder = petReminderRepository.findById(reminderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhắc nhở"));

        if (!reminder.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền xóa nhắc nhở này");
        }

        petReminderRepository.delete(reminder);
    }

    @Override
    public void toggleComplete(Integer reminderId, Integer userId) {
        PetReminder reminder = petReminderRepository.findById(reminderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhắc nhở"));

        if (!reminder.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền thay đổi nhắc nhở này");
        }

        reminder.setIsCompleted(!reminder.getIsCompleted());
        petReminderRepository.save(reminder);
    }

    @Override
    public PetReminder getReminderById(Integer reminderId) {
        return petReminderRepository.findById(reminderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhắc nhở"));
    }
}