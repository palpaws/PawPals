package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PetMoodHistory")
public class PetMoodHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer moodId;

    @ManyToOne
    @JoinColumn(name = "PetId")
    private Pet pet;

    @Column(nullable = false)
    private String mood;

    private String description;

    private LocalDate moodDate;
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