package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PetMatchingPosts")
public class PetMatchingPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @ManyToOne
    @JoinColumn(name = "PetId")
    private Pet pet;

    private String title;

    @Column(length = 1000)
    private String description;

    private String location;

    private LocalDateTime createdAt;

    private String status;

    // ===== Getter & Setter =====

    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}