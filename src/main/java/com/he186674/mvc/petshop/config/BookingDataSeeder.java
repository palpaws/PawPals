package com.he186674.mvc.petshop.config;

import com.he186674.mvc.petshop.entities.ServiceSlot;
import com.he186674.mvc.petshop.entities.Shop;
import com.he186674.mvc.petshop.entities.ShopService;
import com.he186674.mvc.petshop.repository.ServiceSlotRepository;
import com.he186674.mvc.petshop.repository.ShopRepository;
import com.he186674.mvc.petshop.repository.ShopServiceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Tạo dữ liệu mẫu cho tính năng đặt lịch dịch vụ:
 * - Một vài shop cung cấp dịch vụ
 * - Các dịch vụ (khám, spa, phối giống, vaccine)
 * - Khung giờ trống cho MỖI NGÀY từ đầu tháng hiện tại đến hết tháng 8
 *
 * Chỉ chạy khi bảng shops còn rỗng để tránh tạo trùng.
 * Yêu cầu: các bảng đã được tạo sẵn bằng booking_schema.sql
 */
@Component
@Order(1)
public class BookingDataSeeder implements CommandLineRunner {

    private final ShopRepository shopRepository;
    private final ShopServiceRepository shopServiceRepository;
    private final ServiceSlotRepository serviceSlotRepository;

    public BookingDataSeeder(ShopRepository shopRepository,
                             ShopServiceRepository shopServiceRepository,
                             ServiceSlotRepository serviceSlotRepository) {
        this.shopRepository = shopRepository;
        this.shopServiceRepository = shopServiceRepository;
        this.serviceSlotRepository = serviceSlotRepository;
    }

    // Các khung giờ mẫu trong ngày
    private static final String[][] TIME_SLOTS = {
            {"08:00", "09:00"},
            {"09:30", "10:30"},
            {"13:30", "14:30"},
            {"15:00", "16:00"},
            {"16:30", "17:30"}
    };

    @Override
    public void run(String... args) {
        try {
            if (shopRepository.count() > 0) {
                return; // đã có dữ liệu, không seed lại
            }

            // ===== 1. Tạo shops =====
            Shop shop1 = createShop(
                    "PawPals Vet Clinic",
                    "12 Nguyễn Trãi, Thanh Xuân, Hà Nội",
                    "0901234567",
                    "Phòng khám thú y uy tín với đội ngũ bác sĩ giàu kinh nghiệm.",
                    4.8);
            Shop shop2 = createShop(
                    "Happy Pet Spa & Grooming",
                    "45 Lê Văn Lương, Cầu Giấy, Hà Nội",
                    "0912345678",
                    "Dịch vụ spa, tắm rửa, cắt tỉa lông chuyên nghiệp cho thú cưng.",
                    4.6);
            Shop shop3 = createShop(
                    "Pet Care Center",
                    "88 Trần Duy Hưng, Cầu Giấy, Hà Nội",
                    "0987654321",
                    "Trung tâm chăm sóc toàn diện: khám bệnh, tiêm phòng, phối giống.",
                    4.9);

            shopRepository.save(shop1);
            shopRepository.save(shop2);
            shopRepository.save(shop3);

            // ===== 2. Tạo dịch vụ cho từng shop =====
            List<ShopService> services = new ArrayList<>();

            services.add(createService(shop1, "KHAM", "Khám sức khỏe tổng quát",
                    "Kiểm tra sức khỏe toàn diện cho thú cưng.", 300000.0, 60, 50));
            services.add(createService(shop1, "VACCINE", "Tiêm phòng vaccine",
                    "Tiêm các loại vaccine phòng bệnh.", 250000.0, 30, 50));

            services.add(createService(shop2, "SPA", "Spa & Tắm rửa",
                    "Tắm, sấy, vệ sinh tai và cắt móng.", 200000.0, 90, 50));
            services.add(createService(shop2, "SPA", "Cắt tỉa lông tạo kiểu",
                    "Cắt tỉa, tạo kiểu lông theo yêu cầu.", 350000.0, 120, 50));

            services.add(createService(shop3, "KHAM", "Khám chuyên khoa",
                    "Khám chuyên sâu theo triệu chứng.", 400000.0, 60, 50));
            services.add(createService(shop3, "PHOI_GIONG", "Tư vấn & Phối giống",
                    "Dịch vụ tư vấn và hỗ trợ phối giống.", 500000.0, 60, 50));

            for (ShopService s : services) {
                shopServiceRepository.save(s);
            }

            // ===== 3. Tạo slot mỗi ngày từ đầu tháng hiện tại đến hết tháng 8 =====
            LocalDate today = LocalDate.now();
            LocalDate startDate = today.withDayOfMonth(1);
            int endYear = today.getMonthValue() > Month.AUGUST.getValue() ? today.getYear() + 1 : today.getYear();
            LocalDate endDate = LocalDate.of(endYear, Month.AUGUST, 31);

            List<ServiceSlot> slots = new ArrayList<>();
            for (ShopService service : services) {
                LocalDate d = startDate;
                while (!d.isAfter(endDate)) {
                    for (String[] time : TIME_SLOTS) {
                        ServiceSlot slot = new ServiceSlot();
                        slot.setService(service);
                        slot.setSlotDate(d);
                        slot.setStartTime(time[0]);
                        slot.setEndTime(time[1]);
                        slot.setCapacity(3);
                        slot.setBookedCount(0);
                        slots.add(slot);
                    }
                    d = d.plusDays(1);
                }
            }
            serviceSlotRepository.saveAll(slots);

            System.out.println("[BookingDataSeeder] Đã tạo " + services.size()
                    + " dịch vụ và " + slots.size() + " khung giờ từ "
                    + startDate + " đến " + endDate);

        } catch (Exception e) {
            // Nếu bảng chưa được tạo (chưa chạy booking_schema.sql) thì bỏ qua, không làm sập app
            System.err.println("[BookingDataSeeder] Bỏ qua seed dữ liệu: " + e.getMessage());
        }
    }

    private Shop createShop(String name, String address, String phone, String desc, double rating) {
        Shop shop = new Shop();
        shop.setName(name);
        shop.setAddress(address);
        shop.setPhone(phone);
        shop.setDescription(desc);
        shop.setRating(rating);
        shop.setIsActive(true);
        shop.setCreatedAt(LocalDateTime.now());
        return shop;
    }

    private ShopService createService(Shop shop, String type, String name, String desc,
                                      double price, int duration, int depositPercent) {
        ShopService service = new ShopService();
        service.setShop(shop);
        service.setServiceType(type);
        service.setName(name);
        service.setDescription(desc);
        service.setPrice(price);
        service.setDurationMinutes(duration);
        service.setDepositPercent(depositPercent);
        service.setIsActive(true);
        return service;
    }
}
