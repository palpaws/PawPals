package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "StreakRewards")
public class StreakReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rewardId;

    @Column(nullable = false, unique = true)
    private Integer requiredDays;

    @Column(nullable = false)
    private String rewardName;

    private String description;

    private String badgeIconUrl;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "reward")
    private List<UserReward> userRewards;

    // ===== Getter & Setter =====

    public Integer getRewardId() { return rewardId; }
    public void setRewardId(Integer rewardId) { this.rewardId = rewardId; }

    public Integer getRequiredDays() { return requiredDays; }
    public void setRequiredDays(Integer requiredDays) { this.requiredDays = requiredDays; }

    public String getRewardName() { return rewardName; }
    public void setRewardName(String rewardName) { this.rewardName = rewardName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBadgeIconUrl() { return badgeIconUrl; }
    public void setBadgeIconUrl(String badgeIconUrl) { this.badgeIconUrl = badgeIconUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<UserReward> getUserRewards() { return userRewards; }
    public void setUserRewards(List<UserReward> userRewards) { this.userRewards = userRewards; }
}
