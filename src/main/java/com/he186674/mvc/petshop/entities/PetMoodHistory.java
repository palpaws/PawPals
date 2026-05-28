package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_mood_history")
public class PetMoodHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mood_id")
    private Integer moodId;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Column(name = "mood", nullable = false)
    private String mood;

    @Column(name = "description")
    private String description;

    @Column(name = "mood_date")
    private LocalDate moodDate;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===== Getter & Setter =====

    public Integer getMoodId() { return moodId; }
    public void setMoodId(Integer moodId) { this.moodId = moodId; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getMoodDate() { return moodDate; }
    public void setMoodDate(LocalDate moodDate) { this.moodDate = moodDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}