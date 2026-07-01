-- =====================================================================
-- BOOKING / SERVICE SCHEDULING SCHEMA
-- Chuyển trang Lịch từ "nhắc nhở cá nhân" sang "đặt lịch dịch vụ từ shop"
-- Chạy file này trên database PetCareSocialSystem (SQL Server)
-- =====================================================================

-- 1. Cửa hàng / nhà cung cấp dịch vụ
CREATE TABLE shops
(
    shop_id     INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(150) NOT NULL,
    address     NVARCHAR(255),
    phone       NVARCHAR(20),
    description NVARCHAR(1000),
    image_url   NVARCHAR(255),
    rating      DECIMAL(3, 1) DEFAULT 5.0,
    is_active   BIT           DEFAULT 1,
    created_at  DATETIME2     DEFAULT SYSDATETIME()
);

-- 2. Dịch vụ mà shop cung cấp
CREATE TABLE shop_services
(
    service_id       INT IDENTITY(1,1) PRIMARY KEY,
    shop_id          INT NOT NULL,
    service_type     NVARCHAR(50) NOT NULL, -- KHAM, SPA, PHOI_GIONG, VACCINE, OTHER
    name             NVARCHAR(150) NOT NULL,
    description      NVARCHAR(500),
    price            DECIMAL(18, 2) NOT NULL DEFAULT 0,
    duration_minutes INT DEFAULT 60,
    deposit_percent  INT DEFAULT 50,        -- % cọc yêu cầu
    is_active        BIT DEFAULT 1,

    CONSTRAINT fk_service_shop FOREIGN KEY (shop_id)
        REFERENCES shops (shop_id)
        ON DELETE CASCADE
);
CREATE INDEX ix_shop_services_shop ON shop_services (shop_id);
CREATE INDEX ix_shop_services_type ON shop_services (service_type);

-- 3. Khung giờ trống do shop cung cấp
CREATE TABLE service_slots
(
    slot_id      INT IDENTITY(1,1) PRIMARY KEY,
    service_id   INT NOT NULL,
    slot_date    DATE NOT NULL,
    start_time   NVARCHAR(10) NOT NULL,  -- 'HH:mm'
    end_time     NVARCHAR(10),
    capacity     INT DEFAULT 1,
    booked_count INT DEFAULT 0,

    CONSTRAINT fk_slot_service FOREIGN KEY (service_id)
        REFERENCES shop_services (service_id)
        ON DELETE CASCADE
);
CREATE INDEX ix_service_slots_service_date ON service_slots (service_id, slot_date);
CREATE INDEX ix_service_slots_date ON service_slots (slot_date);

-- 4. Lượt đăng ký lịch của khách
CREATE TABLE service_bookings
(
    booking_id     INT IDENTITY(1,1) PRIMARY KEY,
    user_id        INT NOT NULL,
    pet_id         INT NULL,
    slot_id        INT NOT NULL,
    status         NVARCHAR(20) DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','CONFIRMED','CANCELLED','COMPLETED')),
    payment_type   NVARCHAR(20) DEFAULT 'NONE'
        CHECK (payment_type IN ('NONE','DEPOSIT','FULL')),
    total_amount   DECIMAL(18, 2) DEFAULT 0,
    amount_paid    DECIMAL(18, 2) DEFAULT 0,
    note           NVARCHAR(500),
    created_at     DATETIME2 DEFAULT SYSDATETIME(),

    CONSTRAINT fk_booking_user FOREIGN KEY (user_id)
        REFERENCES users (user_id),
    CONSTRAINT fk_booking_pet FOREIGN KEY (pet_id)
        REFERENCES pets (pet_id),
    CONSTRAINT fk_booking_slot FOREIGN KEY (slot_id)
        REFERENCES service_slots (slot_id)
);
CREATE INDEX ix_bookings_user ON service_bookings (user_id);
CREATE INDEX ix_bookings_slot ON service_bookings (slot_id);
