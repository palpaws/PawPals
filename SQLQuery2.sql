CREATE TABLE Users (
    UserId INT IDENTITY(1,1) PRIMARY KEY,
    FullName NVARCHAR(150) NOT NULL,
    Email NVARCHAR(150) NOT NULL UNIQUE,
    PasswordHash NVARCHAR(255) NOT NULL,
    Phone NVARCHAR(20) UNIQUE,
    Address NVARCHAR(255),

    Role NVARCHAR(20) DEFAULT 'USER'
        CHECK (Role IN ('USER','ADMIN')),

    -- 🔥 Streak System
    CurrentStreak INT DEFAULT 0,
    LongestStreak INT DEFAULT 0,
    LastLoginDate DATE NULL,

    CreatedAt DATETIME2 DEFAULT SYSDATETIME(),
    IsActive BIT DEFAULT 1
);

CREATE INDEX IX_Users_Email ON Users(Email);


CREATE TABLE Pets (
    PetId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT NOT NULL,

    PetName NVARCHAR(100) NOT NULL,
    Species NVARCHAR(50) NOT NULL,
    Breed NVARCHAR(100),
    Gender NVARCHAR(10)
        CHECK (Gender IN ('Male','Female')),
    DateOfBirth DATE,
    Birthday DATE NULL,
    Weight DECIMAL(5,2) CHECK (Weight >= 0),
    Height DECIMAL(5,2) NULL,
    Color NVARCHAR(50),

    MicrochipCode NVARCHAR(100) UNIQUE,

    -- Sức khỏe
    BloodType NVARCHAR(10),
    Allergies NVARCHAR(500),
    ChronicDiseases NVARCHAR(500),
    LastVetVisit DATE,
    Vaccinated BIT DEFAULT 0,
    Sterilized BIT DEFAULT 0,
    HealthStatus NVARCHAR(255),

    -- Hành vi
    Temperament NVARCHAR(255),
    ActivityLevel NVARCHAR(50),

    -- Ghép đôi
    IsAvailableForMatching BIT DEFAULT 0,
    MatchingDescription NVARCHAR(500),

    -- Hệ thống
    ProfileViewCount INT DEFAULT 0,
    IsPublic BIT DEFAULT 1,
    Status NVARCHAR(20) DEFAULT 'ACTIVE'
        CHECK (Status IN ('ACTIVE','INACTIVE','DECEASED')),

    CreatedAt DATETIME2 DEFAULT SYSDATETIME(),
    UpdatedAt DATETIME2 NULL,

    CONSTRAINT FK_Pets_User FOREIGN KEY (UserId)
        REFERENCES Users(UserId)
        ON DELETE CASCADE
);

CREATE INDEX IX_Pets_UserId ON Pets(UserId);


CREATE TABLE PetImages (
    ImageId INT IDENTITY(1,1) PRIMARY KEY,
    PetId INT NOT NULL,
    ImageUrl NVARCHAR(255) NOT NULL,
    IsPrimary BIT DEFAULT 0,

    CONSTRAINT FK_PetImages_Pet FOREIGN KEY (PetId)
        REFERENCES Pets(PetId)
        ON DELETE CASCADE
);


CREATE TABLE PetMoodHistory (
    MoodId INT IDENTITY(1,1) PRIMARY KEY,
    PetId INT NOT NULL,
    Mood NVARCHAR(50) NOT NULL,
    Description NVARCHAR(500),
    MoodDate DATE DEFAULT CAST(GETDATE() AS DATE),
    CreatedAt DATETIME2 DEFAULT SYSDATETIME(),

    CONSTRAINT FK_Mood_Pet FOREIGN KEY (PetId)
        REFERENCES Pets(PetId)
        ON DELETE CASCADE
);


CREATE TABLE Vaccines (
    VaccineId INT IDENTITY(1,1) PRIMARY KEY,
    VaccineName NVARCHAR(150) NOT NULL,
    Description NVARCHAR(1000),
    RecommendedMonths INT
);



CREATE TABLE PetVaccinations (
    PetVaccinationId INT IDENTITY(1,1) PRIMARY KEY,
    PetId INT NOT NULL,
    VaccineId INT NOT NULL,
    InjectionDate DATE,
    NextDueDate DATE,
    Status NVARCHAR(20) DEFAULT 'PENDING'
        CHECK (Status IN ('PENDING','COMPLETED','OVERDUE')),

    CONSTRAINT FK_PV_Pet FOREIGN KEY (PetId)
        REFERENCES Pets(PetId)
        ON DELETE CASCADE,

    CONSTRAINT FK_PV_Vaccine FOREIGN KEY (VaccineId)
        REFERENCES Vaccines(VaccineId)
);


CREATE TABLE Notifications (
    NotificationId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT NOT NULL,
    Title NVARCHAR(200),
    Content NVARCHAR(1000),
    IsRead BIT DEFAULT 0,
    CreatedAt DATETIME2 DEFAULT SYSDATETIME(),

    CONSTRAINT FK_Notification_User FOREIGN KEY (UserId)
        REFERENCES Users(UserId)
        ON DELETE CASCADE
);


CREATE TABLE PetMatchingPosts (
    PostId INT IDENTITY(1,1) PRIMARY KEY,
    PetId INT NOT NULL,
    Title NVARCHAR(150),
    Description NVARCHAR(1000),
    Location NVARCHAR(150),
    CreatedAt DATETIME2 DEFAULT SYSDATETIME(),
    Status NVARCHAR(20) DEFAULT 'OPEN'
        CHECK (Status IN ('OPEN','CLOSED')),

    CONSTRAINT FK_Matching_Pet FOREIGN KEY (PetId)
        REFERENCES Pets(PetId)
        ON DELETE CASCADE
);


CREATE TABLE BlogCategories (
    CategoryId INT IDENTITY(1,1) PRIMARY KEY,
    CategoryName NVARCHAR(100) NOT NULL UNIQUE,
    Description NVARCHAR(255)
);


CREATE TABLE BlogPosts (
    PostId INT IDENTITY(1,1) PRIMARY KEY,
    AuthorId INT NOT NULL,
    CategoryId INT NOT NULL,
    Title NVARCHAR(200) NOT NULL,
    Slug NVARCHAR(250) UNIQUE,
    ThumbnailUrl NVARCHAR(255),
    Content NVARCHAR(MAX) NOT NULL,
    ViewCount INT DEFAULT 0,
    CreatedAt DATETIME2 DEFAULT SYSDATETIME(),
    Status NVARCHAR(20) DEFAULT 'PUBLISHED'
        CHECK (Status IN ('DRAFT','PUBLISHED','HIDDEN')),

    CONSTRAINT FK_Blog_Author FOREIGN KEY (AuthorId)
        REFERENCES Users(UserId),

    CONSTRAINT FK_Blog_Category FOREIGN KEY (CategoryId)
        REFERENCES BlogCategories(CategoryId)
);



CREATE TABLE BlogComments (
    CommentId INT IDENTITY(1,1) PRIMARY KEY,
    PostId INT NOT NULL,
    UserId INT NOT NULL,
    Content NVARCHAR(1000) NOT NULL,
    CreatedAt DATETIME2 DEFAULT SYSDATETIME(),

    CONSTRAINT FK_Comment_Post FOREIGN KEY (PostId)
        REFERENCES BlogPosts(PostId)
        ON DELETE CASCADE,

    CONSTRAINT FK_Comment_User FOREIGN KEY (UserId)
        REFERENCES Users(UserId)
);


CREATE TABLE BlogLikes (
    PostId INT NOT NULL,
    UserId INT NOT NULL,
    LikedAt DATETIME2 DEFAULT SYSDATETIME(),

    PRIMARY KEY (PostId, UserId),

    CONSTRAINT FK_Like_Post FOREIGN KEY (PostId)
        REFERENCES BlogPosts(PostId)
        ON DELETE CASCADE,

    CONSTRAINT FK_Like_User FOREIGN KEY (UserId)
        REFERENCES Users(UserId)
);


CREATE TABLE ProductCategories (
    CategoryId INT IDENTITY(1,1) PRIMARY KEY,
    CategoryName NVARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE Products (
    ProductId INT IDENTITY(1,1) PRIMARY KEY,
    CategoryId INT NOT NULL,
    ProductName NVARCHAR(150) NOT NULL,
    Description NVARCHAR(2000),
    Price DECIMAL(18,2) CHECK (Price >= 0),
    Stock INT DEFAULT 0 CHECK (Stock >= 0),
    ImageUrl NVARCHAR(255),
    CreatedAt DATETIME2 DEFAULT SYSDATETIME(),
    IsActive BIT DEFAULT 1,

    CONSTRAINT FK_Product_Category FOREIGN KEY (CategoryId)
        REFERENCES ProductCategories(CategoryId)
);

CREATE TABLE Orders (
    OrderId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT NOT NULL,
    OrderDate DATETIME2 DEFAULT SYSDATETIME(),
    TotalAmount DECIMAL(18,2),
    Status NVARCHAR(30) DEFAULT 'PENDING'
        CHECK (Status IN ('PENDING','CONFIRMED','SHIPPING','COMPLETED','CANCELLED')),

    ShippingAddress NVARCHAR(255),
    Phone NVARCHAR(20),

    CONSTRAINT FK_Order_User FOREIGN KEY (UserId)
        REFERENCES Users(UserId)
);



CREATE TABLE OrderDetails (
    OrderDetailId INT IDENTITY(1,1) PRIMARY KEY,
    OrderId INT NOT NULL,
    ProductId INT NOT NULL,
    Quantity INT CHECK (Quantity > 0),
    UnitPrice DECIMAL(18,2),

    CONSTRAINT FK_OD_Order FOREIGN KEY (OrderId)
        REFERENCES Orders(OrderId)
        ON DELETE CASCADE,

    CONSTRAINT FK_OD_Product FOREIGN KEY (ProductId)
        REFERENCES Products(ProductId)
);


CREATE TABLE StreakRewards (
    RewardId INT IDENTITY(1,1) PRIMARY KEY,
    RequiredDays INT NOT NULL UNIQUE,  -- số ngày cần đạt
    RewardName NVARCHAR(100) NOT NULL,
    Description NVARCHAR(255),
    BadgeIconUrl NVARCHAR(255),        -- icon huy hiệu (nếu có)
    CreatedAt DATETIME2 DEFAULT SYSDATETIME()
);
INSERT INTO StreakRewards (RequiredDays, RewardName, Description)
VALUES 
(7, 'Bronze Badge', 'Giữ lửa 7 ngày liên tiếp'),
(30, 'Silver Badge', 'Giữ lửa 30 ngày liên tiếp'),
(100, 'Golden Flame', 'Giữ lửa 100 ngày liên tiếp');


CREATE TABLE UserRewards (
    UserRewardId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT NOT NULL,
    RewardId INT NOT NULL,
    AwardedAt DATETIME2 DEFAULT SYSDATETIME(),

    CONSTRAINT UQ_User_Reward UNIQUE (UserId, RewardId),

    CONSTRAINT FK_UserReward_User FOREIGN KEY (UserId)
        REFERENCES Users(UserId)
        ON DELETE CASCADE,

    CONSTRAINT FK_UserReward_Reward FOREIGN KEY (RewardId)
        REFERENCES StreakRewards(RewardId)
        ON DELETE CASCADE
);
