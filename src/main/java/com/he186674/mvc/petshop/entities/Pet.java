package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer petId;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    private String petName;
    private String species;
    private String breed;
    private String gender;

    private LocalDate dateOfBirth;
    private LocalDate birthday;

    private Double weight;
    private Double height;

    private String color;
    private String microchipCode;

    private String bloodType;
    private String allergies;
    private String chronicDiseases;
    private LocalDate lastVetVisit;

    private Boolean vaccinated;
    private Boolean sterilized;

    private String healthStatus;
    private String temperament;
    private String activityLevel;

    private Boolean isAvailableForMatching;
    private String matchingDescription;

    private Integer profileViewCount;
    private Boolean isPublic;

    private String status;

    private LocalDateTime createdAt;
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