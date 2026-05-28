package com.he186674.mvc.petshop.entities;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_rewards",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "reward_id"}))
public class UserReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_reward_id")
    private Integer userRewardId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "reward_id")
    private StreakReward reward;

    @Column(name = "awarded_at")
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