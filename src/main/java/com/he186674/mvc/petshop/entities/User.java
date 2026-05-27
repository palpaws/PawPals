package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String passwordHash;

    @Column(unique = true)
    private String phone;

    private String address;

    private String role = "USER";

    private Integer currentStreak = 0;
    private Integer longestStreak = 0;

    private LocalDate lastLoginDate;

    private LocalDateTime createdAt;

    private Boolean isActive = true;

    // ===== RELATIONSHIPS =====

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Pet> pets;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
    private List<UserReward> rewards;

    // ===== GETTER & SETTER =====

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }

    public Integer getLongestStreak() { return longestStreak; }
    public void setLongestStreak(Integer longestStreak) { this.longestStreak = longestStreak; }

    public LocalDate getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDate lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public List<Pet> getPets() { return pets; }
    public void setPets(List<Pet> pets) { this.pets = pets; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }

    public List<UserReward> getRewards() { return rewards; }
    public void setRewards(List<UserReward> rewards) { this.rewards = rewards; }
}