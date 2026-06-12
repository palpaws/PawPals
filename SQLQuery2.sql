CREATE TABLE users
(
    user_id            INT IDENTITY(1,1) PRIMARY KEY,
    full_name          NVARCHAR(150) NOT NULL,
    email              NVARCHAR(150) NOT NULL UNIQUE,
    password_hash      NVARCHAR(255) NOT NULL,
    phone              NVARCHAR(20),
    address            NVARCHAR(255),
    role               NVARCHAR(20) DEFAULT 'USER'
        CHECK (role IN ('USER','ADMIN')),
    premium_expired_at DATETIME2 NULL,
    current_streak     INT       DEFAULT 0,
    longest_streak     INT       DEFAULT 0,
    last_login_date    DATE NULL,
    reset_token        NVARCHAR(255) NULL,
    reset_token_expiry DATETIME2 NULL,
    otp                NVARCHAR(10) NULL,
    otp_expiry         DATETIME2 NULL,
    created_at         DATETIME2 DEFAULT SYSDATETIME(),
    is_active          BIT       DEFAULT 1
);
CREATE INDEX ix_users_email ON users (email);

CREATE TABLE pets
(
    pet_id                    INT IDENTITY(1,1) PRIMARY KEY,
    user_id                   INT NOT NULL,

    pet_name                  NVARCHAR(100) NOT NULL,
    species                   NVARCHAR(50) NOT NULL,
    breed                     NVARCHAR(100),
    gender                    NVARCHAR(10)
        CHECK (gender IN ('Male','Female')),
    date_of_birth             DATE,
    birthday                  DATE NULL,
    weight                    DECIMAL(5, 2) CHECK (weight >= 0),
    height                    DECIMAL(5, 2) NULL,
    color                     NVARCHAR(50),

    microchip_code            NVARCHAR(100) UNIQUE,

    -- Sức khỏe
    blood_type                NVARCHAR(10),
    allergies                 NVARCHAR(500),
    chronic_diseases          NVARCHAR(500),
    last_vet_visit            DATE,
    vaccinated                BIT       DEFAULT 0,
    sterilized                BIT       DEFAULT 0,
    health_status             NVARCHAR(255),

    -- Hành vi
    temperament               NVARCHAR(255),
    activity_level            NVARCHAR(50),

    -- Ghép đôi
    is_available_for_matching BIT       DEFAULT 0,
    matching_description      NVARCHAR(500),

    -- Hệ thống
    profile_view_count        INT       DEFAULT 0,
    is_public                 BIT       DEFAULT 1,
    status                    NVARCHAR(20) DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE','INACTIVE','DECEASED')),

    created_at                DATETIME2 DEFAULT SYSDATETIME(),
    updated_at                DATETIME2 NULL,

    CONSTRAINT fk_pets_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE
);

CREATE INDEX ix_pets_user_id ON pets (user_id);


CREATE TABLE pet_images
(
    image_id   INT IDENTITY(1,1) PRIMARY KEY,
    pet_id     INT NOT NULL,
    image_url  NVARCHAR(255) NOT NULL,
    is_primary BIT DEFAULT 0,

    CONSTRAINT fk_pet_images_pet FOREIGN KEY (pet_id)
        REFERENCES pets (pet_id)
        ON DELETE CASCADE
);


CREATE TABLE pet_mood_history
(
    mood_id     INT IDENTITY(1,1) PRIMARY KEY,
    pet_id      INT NOT NULL,
    mood        NVARCHAR(50) NOT NULL,
    description NVARCHAR(500),
    mood_date   DATE      DEFAULT CAST(GETDATE() AS DATE),
    created_at  DATETIME2 DEFAULT SYSDATETIME(),

    CONSTRAINT fk_mood_pet FOREIGN KEY (pet_id)
        REFERENCES pets (pet_id)
        ON DELETE CASCADE
);


CREATE TABLE vaccines
(
    vaccine_id         INT IDENTITY(1,1) PRIMARY KEY,
    vaccine_name       NVARCHAR(150) NOT NULL,
    description        NVARCHAR(1000),
    recommended_months INT
);



CREATE TABLE pet_vaccinations
(
    pet_vaccination_id INT IDENTITY(1,1) PRIMARY KEY,
    pet_id             INT NOT NULL,
    vaccine_id         INT NOT NULL,
    injection_date     DATE,
    next_due_date      DATE,
    status             NVARCHAR(20) DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','COMPLETED','OVERDUE')),

    CONSTRAINT fk_pv_pet FOREIGN KEY (pet_id)
        REFERENCES pets (pet_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pv_vaccine FOREIGN KEY (vaccine_id)
        REFERENCES vaccines (vaccine_id)
);


CREATE TABLE notifications
(
    notification_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id         INT       NOT NULL,
    reminder_id     INT NULL,
    type            NVARCHAR(50) NOT NULL,
    title           NVARCHAR(200) NOT NULL,
    content         NVARCHAR(1000) NOT NULL,
    is_read         BIT       NOT NULL DEFAULT 0,
    created_at      DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON DELETE CASCADE

);

CREATE TABLE pet_matching_posts
(
    post_id     INT IDENTITY(1,1) PRIMARY KEY,
    pet_id      INT NOT NULL,
    title       NVARCHAR(150),
    description NVARCHAR(1000),
    location    NVARCHAR(150),
    created_at  DATETIME2 DEFAULT SYSDATETIME(),
    status      NVARCHAR(20) DEFAULT 'OPEN'
        CHECK (status IN ('OPEN','CLOSED')),

    CONSTRAINT fk_matching_pet FOREIGN KEY (pet_id)
        REFERENCES pets (pet_id)
        ON DELETE CASCADE
);


CREATE TABLE blog_categories
(
    category_id   INT IDENTITY(1,1) PRIMARY KEY,
    category_name NVARCHAR(100) NOT NULL UNIQUE,
    description   NVARCHAR(255)
);


CREATE TABLE blog_posts
(
    post_id       INT IDENTITY(1,1) PRIMARY KEY,
    author_id     INT NOT NULL,
    category_id   INT NOT NULL,
    title         NVARCHAR(200) NOT NULL,
    slug          NVARCHAR(250) UNIQUE,
    thumbnail_url NVARCHAR(255),
    content       NVARCHAR(MAX) NOT NULL,
    view_count    INT       DEFAULT 0,
    created_at    DATETIME2 DEFAULT SYSDATETIME(),
    status        NVARCHAR(20) DEFAULT 'PUBLISHED'
        CHECK (status IN ('DRAFT','PUBLISHED','HIDDEN')),

    CONSTRAINT fk_blog_author FOREIGN KEY (author_id)
        REFERENCES users (user_id),

    CONSTRAINT fk_blog_category FOREIGN KEY (category_id)
        REFERENCES blog_categories (category_id)
);



CREATE TABLE blog_comments
(
    comment_id INT IDENTITY(1,1) PRIMARY KEY,
    post_id    INT NOT NULL,
    user_id    INT NOT NULL,
    content    NVARCHAR(1000) NOT NULL,
    created_at DATETIME2 DEFAULT SYSDATETIME(),

    CONSTRAINT fk_comment_post FOREIGN KEY (post_id)
        REFERENCES blog_posts (post_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comment_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
);


CREATE TABLE blog_likes
(
    post_id  INT NOT NULL,
    user_id  INT NOT NULL,
    liked_at DATETIME2 DEFAULT SYSDATETIME(),

    PRIMARY KEY (post_id, user_id),

    CONSTRAINT fk_like_post FOREIGN KEY (post_id)
        REFERENCES blog_posts (post_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_like_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
);


CREATE TABLE product_categories
(
    category_id   INT IDENTITY(1,1) PRIMARY KEY,
    category_name NVARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE products
(
    product_id   INT IDENTITY(1,1) PRIMARY KEY,
    category_id  INT NOT NULL,
    product_name NVARCHAR(150) NOT NULL,
    description  NVARCHAR(2000),
    price        DECIMAL(18, 2) CHECK (price >= 0),
    stock        INT       DEFAULT 0 CHECK (stock >= 0),
    image_url    NVARCHAR(255),
    created_at   DATETIME2 DEFAULT SYSDATETIME(),
    is_active    BIT       DEFAULT 1,

    CONSTRAINT fk_product_category FOREIGN KEY (category_id)
        REFERENCES product_categories (category_id)
);

CREATE TABLE orders
(
    order_id         INT IDENTITY(1,1) PRIMARY KEY,
    user_id          INT NOT NULL,
    order_date       DATETIME2 DEFAULT SYSDATETIME(),
    total_amount     DECIMAL(18, 2),
    status           NVARCHAR(30) DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','CONFIRMED','SHIPPING','COMPLETED','CANCELLED')),

    shipping_address NVARCHAR(255),
    phone            NVARCHAR(20),

    CONSTRAINT fk_order_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
);



CREATE TABLE order_details
(
    order_detail_id INT IDENTITY(1,1) PRIMARY KEY,
    order_id        INT NOT NULL,
    product_id      INT NOT NULL,
    quantity        INT CHECK (quantity > 0),
    unit_price      DECIMAL(18, 2),

    CONSTRAINT fk_od_order FOREIGN KEY (order_id)
        REFERENCES orders (order_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_od_product FOREIGN KEY (product_id)
        REFERENCES products (product_id)
);


CREATE TABLE streak_rewards
(
    reward_id      INT IDENTITY(1,1) PRIMARY KEY,
    required_days  INT NOT NULL UNIQUE, -- số ngày cần đạt
    reward_name    NVARCHAR(100) NOT NULL,
    description    NVARCHAR(255),
    badge_icon_url NVARCHAR(255),       -- icon huy hiệu (nếu có)
    created_at     DATETIME2 DEFAULT SYSDATETIME()
);
INSERT INTO streak_rewards (required_days, reward_name, description)
VALUES (7, N'Bronze Badge', N'Giữ lửa 7 ngày liên tiếp'),
       (30, N'Silver Badge', N'Giữ lửa 30 ngày liên tiếp'),
       (100, N'Golden Flame', N'Giữ lửa 100 ngày liên tiếp');

CREATE TABLE user_rewards
(
    user_reward_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id        INT NOT NULL,
    reward_id      INT NOT NULL,
    awarded_at     DATETIME2 DEFAULT SYSDATETIME(),

    CONSTRAINT uq_user_reward UNIQUE (user_id, reward_id),

    CONSTRAINT fk_userreward_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_userreward_reward FOREIGN KEY (reward_id)
        REFERENCES streak_rewards (reward_id)
        ON DELETE CASCADE
);


-- =============================================
-- BẢNG PET_REMINDERS (Nhắc nhở lịch khám, spa, phối giống cho thú cưng)
-- =============================================

CREATE TABLE pet_reminders
(
    reminder_id             INT IDENTITY(1,1) PRIMARY KEY,
    pet_id                  INT  NOT NULL,
    user_id                 INT  NOT NULL,
    title                   NVARCHAR(150) NOT NULL,
    description             NVARCHAR(500),
    event_type              NVARCHAR(50) NOT NULL,
    event_date              DATE NOT NULL,
    event_time              NVARCHAR(20),
    location                NVARCHAR(255),
    is_completed            BIT       DEFAULT 0,
    is_recurring            BIT       DEFAULT 0,
    recurring_interval_days INT,
    created_at              DATETIME2 DEFAULT SYSDATETIME(),

    CONSTRAINT fk_reminder_pet FOREIGN KEY (pet_id)
        REFERENCES pets (pet_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reminder_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
);
