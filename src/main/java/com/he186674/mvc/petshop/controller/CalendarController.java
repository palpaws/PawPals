package com.he186674.mvc.petshop.controller;

import com.he186674.mvc.petshop.entities.Pet;
import com.he186674.mvc.petshop.entities.PetImage;
import com.he186674.mvc.petshop.entities.PetReminder;
import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.repository.PetRepository;
import com.he186674.mvc.petshop.service.PetReminderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CalendarController {

    private final PetReminderService petReminderService;
    private final PetRepository petRepository;

    @Autowired
    public CalendarController(PetReminderService petReminderService, PetRepository petRepository) {
        this.petReminderService = petReminderService;
        this.petRepository = petRepository;
    }

    // Helper: lấy URL ảnh chính của pet
    private String getPrimaryImageUrl(Pet pet) {
        if (pet.getImages() != null && !pet.getImages().isEmpty()) {
            // Ưu tiên ảnh isPrimary == true
            return pet.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                    .findFirst()
                    .map(PetImage::getImageUrl)
                    .orElse(pet.getImages().get(0).getImageUrl());
        }
        return null;
    }

    @GetMapping("/calendar")
    public String calendarPage(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Lấy danh sách pets của user + ảnh chính
        List<Pet> pets = petRepository.findByUser_UserId(currentUser.getUserId());
        model.addAttribute("pets", pets);

        // Build pet image map cho Thymeleaf
        Map<Integer, String> petImageMap = new HashMap<>();
        for (Pet pet : pets) {
            petImageMap.put(pet.getPetId(), getPrimaryImageUrl(pet));
        }
        model.addAttribute("petImageMap", petImageMap);

        // Lấy reminders sắp tới (tất cả pets)
        List<PetReminder> upcomingReminders = petReminderService.getUpcomingReminders(currentUser.getUserId(), null);
        model.addAttribute("upcomingReminders", upcomingReminders);

        return "Calendar";
    }

    // ===== REST API ENDPOINTS =====

    @GetMapping("/api/calendar/reminders")
    @ResponseBody
    public ResponseEntity<?> getReminders(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam(value = "petId", required = false) Integer petId,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<PetReminder> reminders = petReminderService.getRemindersByUserAndMonth(
                currentUser.getUserId(), petId, year, month);

        // Convert to DTO for JSON response
        List<Map<String, Object>> reminderDTOs = reminders.stream().map(r -> {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("reminderId", r.getReminderId());
            dto.put("title", r.getTitle());
            dto.put("description", r.getDescription());
            dto.put("eventType", r.getEventType());
            dto.put("eventDate", r.getEventDate().toString());
            dto.put("eventTime", r.getEventTime());
            dto.put("location", r.getLocation());
            dto.put("isCompleted", r.getIsCompleted());
            dto.put("isRecurring", r.getIsRecurring());
            dto.put("petId", r.getPet().getPetId());
            dto.put("petName", r.getPet().getPetName());
            dto.put("petImageUrl", getPrimaryImageUrl(r.getPet()));
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(reminderDTOs);
    }

    @PostMapping("/api/calendar/reminders")
    @ResponseBody
    public ResponseEntity<?> createReminder(@RequestBody Map<String, String> body, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            Integer petId = Integer.parseInt(body.get("petId"));
            Pet pet = petRepository.findById(petId).orElse(null);
            if (pet == null || !pet.getUser().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.badRequest().body("Thú cưng không hợp lệ");
            }

            PetReminder reminder = new PetReminder();
            reminder.setUser(currentUser);
            reminder.setPet(pet);
            reminder.setTitle(body.get("title"));
            reminder.setDescription(body.get("description"));
            reminder.setEventType(body.get("eventType"));
            reminder.setEventDate(LocalDate.parse(body.get("eventDate")));
            reminder.setEventTime(body.get("eventTime"));
            reminder.setLocation(body.get("location"));
            reminder.setIsCompleted(false);

            if (body.containsKey("isRecurring") && "true".equals(body.get("isRecurring"))) {
                reminder.setIsRecurring(true);
                if (body.containsKey("recurringIntervalDays")) {
                    reminder.setRecurringIntervalDays(Integer.parseInt(body.get("recurringIntervalDays")));
                }
            } else {
                reminder.setIsRecurring(false);
            }

            PetReminder saved = petReminderService.createReminder(reminder);

            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("reminderId", saved.getReminderId());
            dto.put("title", saved.getTitle());
            dto.put("description", saved.getDescription());
            dto.put("eventType", saved.getEventType());
            dto.put("eventDate", saved.getEventDate().toString());
            dto.put("eventTime", saved.getEventTime());
            dto.put("location", saved.getLocation());
            dto.put("isCompleted", saved.getIsCompleted());
            dto.put("isRecurring", saved.getIsRecurring());
            dto.put("petId", saved.getPet().getPetId());
            dto.put("petName", saved.getPet().getPetName());

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PutMapping("/api/calendar/reminders/{reminderId}/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleReminder(@PathVariable Integer reminderId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            petReminderService.toggleComplete(reminderId, currentUser.getUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/calendar/reminders/{reminderId}")
    @ResponseBody
    public ResponseEntity<?> deleteReminder(@PathVariable Integer reminderId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            petReminderService.deleteReminder(reminderId, currentUser.getUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/calendar/upcoming")
    @ResponseBody
    public ResponseEntity<?> getUpcomingReminders(
            @RequestParam(value = "petId", required = false) Integer petId,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<PetReminder> reminders = petReminderService.getUpcomingReminders(currentUser.getUserId(), petId);

        List<Map<String, Object>> reminderDTOs = reminders.stream().map(r -> {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("reminderId", r.getReminderId());
            dto.put("title", r.getTitle());
            dto.put("eventType", r.getEventType());
            dto.put("eventDate", r.getEventDate().toString());
            dto.put("petName", r.getPet().getPetName());
            dto.put("petId", r.getPet().getPetId());
            dto.put("petImageUrl", getPrimaryImageUrl(r.getPet()));
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(reminderDTOs);
    }
}
