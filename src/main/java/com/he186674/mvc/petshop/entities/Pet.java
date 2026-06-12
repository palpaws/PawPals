package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Integer petId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "pet_name")
    private String petName;
    @Column(name = "species")
    private String species;
    @Column(name = "breed")
    private String breed;
    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "weight")
    private Double weight;
    @Column(name = "height")
    private Double height;

    @Column(name = "color")
    private String color;
    @Column(name = "microchip_code")
    private String microchipCode;

    @Column(name = "blood_type")
    private String bloodType;
    @Column(name = "allergies")
    private String allergies;
    @Column(name = "chronic_diseases")
    private String chronicDiseases;
    @Column(name = "last_vet_visit")
    private LocalDate lastVetVisit;

    @Column(name = "vaccinated")
    private Boolean vaccinated;
    @Column(name = "sterilized")
    private Boolean sterilized;

    @Column(name = "health_status")
    private String healthStatus;
    @Column(name = "temperament")
    private String temperament;
    @Column(name = "activity_level")
    private String activityLevel;

    @Column(name = "is_available_for_matching")
    private Boolean isAvailableForMatching;
    @Column(name = "matching_description")
    private String matchingDescription;

    @Column(name = "profile_view_count")
    private Integer profileViewCount;
    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // RELATIONSHIPS
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
    private List<PetImage> images;

    @OneToMany(mappedBy = "pet")
    private List<PetMoodHistory> moods;

    @OneToMany(mappedBy = "pet")
    private List<PetVaccination> vaccinations;

    // ===== GETTER & SETTER (rút gọn hiển thị) =====

    public Integer getPetId() { return petId; }
    public void setPetId(Integer petId) { this.petId = petId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public List<PetImage> getImages() { return images; }
    public void setImages(List<PetImage> images) { this.images = images; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getMicrochipCode() { return microchipCode; }
    public void setMicrochipCode(String microchipCode) { this.microchipCode = microchipCode; }

    public Boolean getVaccinated() { return vaccinated; }
    public void setVaccinated(Boolean vaccinated) { this.vaccinated = vaccinated; }

    public Boolean getSterilized() { return sterilized; }
    public void setSterilized(Boolean sterilized) { this.sterilized = sterilized; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
