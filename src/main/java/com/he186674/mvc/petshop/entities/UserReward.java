package com.he186674.mvc.petshop.entities;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserRewards",
        uniqueConstraints = @UniqueConstraint(columnNames = {"UserId", "RewardId"}))
public class UserReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userRewardId;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "RewardId")
    private StreakReward reward;

    private LocalDateTime awardedAt;

    // ===== Getter & Setter =====

    public Integer getUserRewardId() { return userRewardId; }
    public void setUserRewardId(Integer userRewardId) { this.userRewardId = userRewardId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public StreakReward getReward() { return reward; }
    public void setReward(StreakReward reward) { this.reward = reward; }

    public LocalDateTime getAwardedAt() { return awardedAt; }
    public void setAwardedAt(LocalDateTime awardedAt) { this.awardedAt = awardedAt; }
}