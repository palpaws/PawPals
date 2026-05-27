package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;

@Entity
@Table(name = "PetImages")
public class PetImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;

    @ManyToOne
    @JoinColumn(name = "PetId")
    private Pet pet;

    @Column(nullable = false)
    private String imageUrl;

    private Boolean isPrimary = false;

    // ===== Getter & Setter =====

    public Integer getImageId() { return imageId; }
    public void setImageId(Integer imageId) { this.imageId = imageId; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
}