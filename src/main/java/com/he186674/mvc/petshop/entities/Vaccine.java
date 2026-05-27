package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Vaccines")
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vaccineId;

    @Column(nullable = false)
    private String vaccineName;

    @Column(length = 1000)
    private String description;

    private Integer recommendedMonths;

    @OneToMany(mappedBy = "vaccine")
    private List<PetVaccination> petVaccinations;

    // ===== Getter & Setter =====

    public Integer getVaccineId() { return vaccineId; }
    public void setVaccineId(Integer vaccineId) { this.vaccineId = vaccineId; }

    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getRecommendedMonths() { return recommendedMonths; }
    public void setRecommendedMonths(Integer recommendedMonths) { this.recommendedMonths = recommendedMonths; }

    public List<PetVaccination> getPetVaccinations() { return petVaccinations; }
    public void setPetVaccinations(List<PetVaccination> petVaccinations) { this.petVaccinations = petVaccinations; }
}