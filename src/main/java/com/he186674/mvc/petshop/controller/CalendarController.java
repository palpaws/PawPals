package com.he186674.mvc.petshop.controller;

import com.he186674.mvc.petshop.entities.*;
import com.he186674.mvc.petshop.repository.PetRepository;
import com.he186674.mvc.petshop.repository.ServiceBookingRepository;
import com.he186674.mvc.petshop.repository.ServiceSlotRepository;
import com.he186674.mvc.petshop.repository.ShopServiceRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CalendarController {

    private final PetRepository petRepository;
    private final ShopServiceRepository shopServiceRepository;
    private final ServiceSlotRepository serviceSlotRepository;
    private final ServiceBookingRepository serviceBookingRepository;

    @Autowired
    public CalendarController(PetRepository petRepository,
                              ShopServiceRepository shopServiceRepository,
                              ServiceSlotRepository serviceSlotRepository,
                              ServiceBookingRepository serviceBookingRepository) {
        this.petRepository = petRepository;
        this.shopServiceRepository = shopServiceRepository;
        this.serviceSlotRepository = serviceSlotRepository;
        this.serviceBookingRepository = serviceBookingRepository;
    }

    // Helper: lấy URL ảnh chính của pet
    private String getPrimaryImageUrl(Pet pet) {
        if (pet.getImages() != null && !pet.getImages().isEmpty()) {
            return pet.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                    .findFirst()
                    .map(PetImage::getImageUrl)
                    .orElse(pet.getImages().get(0).getImageUrl());
        }
        return null;
    }

    private static final Map<String, String> SERVICE_TYPE_LABELS = Map.of(
            "KHAM", "Khám bệnh",
            "SPA", "Spa / Tắm rửa",
            "PHOI_GIONG", "Phối giống",
            "VACCINE", "Tiêm chủng",
            "OTHER", "Khác"
    );

    @GetMapping("/calendar")
    public String calendarPage(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Pet> pets = petRepository.findByUser_UserId(currentUser.getUserId());
        model.addAttribute("pets", pets);

        Map<Integer, String> petImageMap = new HashMap<>();
        for (Pet pet : pets) {
            petImageMap.put(pet.getPetId(), getPrimaryImageUrl(pet));
        }
        model.addAttribute("petImageMap", petImageMap);

        return "Calendar";
    }

    // ===== REST API: SERVICES =====

    @GetMapping("/api/booking/services")
    @ResponseBody
    public ResponseEntity<?> getServices(
            @RequestParam(value = "serviceType", required = false) String serviceType,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<ShopService> services;
        if (serviceType != null && !serviceType.isBlank()) {
            services = shopServiceRepository.findByServiceTypeAndIsActiveTrue(serviceType);
        } else {
            services = shopServiceRepository.findByIsActiveTrue();
        }

        List<Map<String, Object>> dtos = services.stream().map(this::serviceToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private Map<String, Object> serviceToDto(ShopService s) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("serviceId", s.getServiceId());
        dto.put("serviceType", s.getServiceType());
        dto.put("serviceTypeLabel", SERVICE_TYPE_LABELS.getOrDefault(s.getServiceType(), s.getServiceType()));
        dto.put("name", s.getName());
        dto.put("description", s.getDescription());
        dto.put("price", s.getPrice());
        dto.put("durationMinutes", s.getDurationMinutes());
        dto.put("depositPercent", s.getDepositPercent());
        if (s.getShop() != null) {
            dto.put("shopId", s.getShop().getShopId());
            dto.put("shopName", s.getShop().getName());
            dto.put("shopAddress", s.getShop().getAddress());
            dto.put("shopPhone", s.getShop().getPhone());
            dto.put("shopRating", s.getShop().getRating());
        }
        return dto;
    }

    // ===== REST API: SLOTS for a service in a given month =====

    @GetMapping("/api/booking/slots")
    @ResponseBody
    public ResponseEntity<?> getSlots(
            @RequestParam("serviceId") Integer serviceId,
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<ServiceSlot> slots = serviceSlotRepository.findByServiceAndDateRange(serviceId, startDate, endDate);
        LocalDate today = LocalDate.now();

        List<Map<String, Object>> dtos = slots.stream().map(slot -> {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("slotId", slot.getSlotId());
            dto.put("slotDate", slot.getSlotDate().toString());
            dto.put("startTime", slot.getStartTime());
            dto.put("endTime", slot.getEndTime());
            dto.put("capacity", slot.getCapacity());
            dto.put("bookedCount", slot.getBookedCount());
            boolean isPast = slot.getSlotDate().isBefore(today);
            boolean isFull = slot.isFull();
            dto.put("isPast", isPast);
            dto.put("isFull", isFull);
            dto.put("bookable", !isPast && !isFull);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ===== REST API: CREATE BOOKING =====

    @PostMapping("/api/booking/register")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> registerBooking(@RequestBody Map<String, String> body, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            Integer slotId = Integer.parseInt(body.get("slotId"));
            ServiceSlot slot = serviceSlotRepository.findById(slotId).orElse(null);
            if (slot == null) {
                return ResponseEntity.badRequest().body("Khung giờ không tồn tại");
            }

            // Chặn đăng ký lịch trong quá khứ
            if (slot.getSlotDate().isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body("Không thể đăng ký lịch trong quá khứ");
            }

            // Chặn slot đã đầy
            if (slot.isFull()) {
                return ResponseEntity.badRequest().body("Khung giờ này đã đầy, vui lòng chọn khung giờ khác");
            }

            // Chặn đăng ký trùng
            boolean already = serviceBookingRepository
                    .existsByUser_UserIdAndSlot_SlotIdAndStatusNot(currentUser.getUserId(), slotId, "CANCELLED");
            if (already) {
                return ResponseEntity.badRequest().body("Bạn đã đăng ký khung giờ này rồi");
            }

            // Pet là optional
            Pet pet = null;
            String petIdStr = body.get("petId");
            if (petIdStr != null && !petIdStr.isBlank()) {
                pet = petRepository.findById(Integer.parseInt(petIdStr)).orElse(null);
                if (pet != null && !pet.getUser().getUserId().equals(currentUser.getUserId())) {
                    return ResponseEntity.badRequest().body("Thú cưng không hợp lệ");
                }
            }

            ShopService service = slot.getService();
            double price = service.getPrice() != null ? service.getPrice() : 0.0;

            // Loại thanh toán: NONE / DEPOSIT / FULL
            String paymentType = body.getOrDefault("paymentType", "NONE");
            double amountPaid;
            if ("FULL".equals(paymentType)) {
                amountPaid = price;
            } else if ("DEPOSIT".equals(paymentType)) {
                int pct = service.getDepositPercent() != null ? service.getDepositPercent() : 50;
                amountPaid = price * pct / 100.0;
            } else {
                paymentType = "NONE";
                amountPaid = 0.0;
            }

            ServiceBooking booking = new ServiceBooking();
            booking.setUser(currentUser);
            booking.setPet(pet);
            booking.setSlot(slot);
            booking.setStatus("CONFIRMED");
            booking.setPaymentType(paymentType);
            booking.setTotalAmount(price);
            booking.setAmountPaid(amountPaid);
            booking.setNote(body.get("note"));
            booking.setCreatedAt(LocalDateTime.now());

            serviceBookingRepository.save(booking);

            // Tăng số lượng đã đặt của slot
            slot.setBookedCount((slot.getBookedCount() == null ? 0 : slot.getBookedCount()) + 1);
            serviceSlotRepository.save(slot);

            Map<String, Object> dto = bookingToDto(booking);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // ===== REST API: MY BOOKINGS =====

    @GetMapping("/api/booking/my")
    @ResponseBody
    public ResponseEntity<?> myBookings(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<ServiceBooking> bookings =
                serviceBookingRepository.findByUser_UserIdOrderByCreatedAtDesc(currentUser.getUserId());

        List<Map<String, Object>> dtos = bookings.stream()
                .map(this::bookingToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ===== REST API: CANCEL BOOKING =====

    @DeleteMapping("/api/booking/{bookingId}")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> cancelBooking(@PathVariable Integer bookingId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            ServiceBooking booking = serviceBookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy lịch đặt");
            }
            if (!booking.getUser().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.badRequest().body("Bạn không có quyền hủy lịch này");
            }
            if ("CANCELLED".equals(booking.getStatus())) {
                return ResponseEntity.ok().build();
            }

            booking.setStatus("CANCELLED");
            serviceBookingRepository.save(booking);

            ServiceSlot slot = booking.getSlot();
            if (slot != null && slot.getBookedCount() != null && slot.getBookedCount() > 0) {
                slot.setBookedCount(slot.getBookedCount() - 1);
                serviceSlotRepository.save(slot);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    private Map<String, Object> bookingToDto(ServiceBooking b) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("bookingId", b.getBookingId());
        dto.put("status", b.getStatus());
        dto.put("paymentType", b.getPaymentType());
        dto.put("totalAmount", b.getTotalAmount());
        dto.put("amountPaid", b.getAmountPaid());
        dto.put("note", b.getNote());

        ServiceSlot slot = b.getSlot();
        if (slot != null) {
            dto.put("slotId", slot.getSlotId());
            dto.put("slotDate", slot.getSlotDate().toString());
            dto.put("startTime", slot.getStartTime());
            dto.put("endTime", slot.getEndTime());

            ShopService service = slot.getService();
            if (service != null) {
                dto.put("serviceId", service.getServiceId());
                dto.put("serviceName", service.getName());
                dto.put("serviceType", service.getServiceType());
                dto.put("serviceTypeLabel",
                        SERVICE_TYPE_LABELS.getOrDefault(service.getServiceType(), service.getServiceType()));
                if (service.getShop() != null) {
                    dto.put("shopName", service.getShop().getName());
                    dto.put("shopAddress", service.getShop().getAddress());
                }
            }
        }

        if (b.getPet() != null) {
            dto.put("petId", b.getPet().getPetId());
            dto.put("petName", b.getPet().getPetName());
        }
        return dto;
    }
}
