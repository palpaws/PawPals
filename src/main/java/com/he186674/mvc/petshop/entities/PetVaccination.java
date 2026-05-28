package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pet_vaccinations")
public class PetVaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_vaccination_id")
    private Integer petVaccinationId;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;

    @Column(name = "injection_date")
    private LocalDate injectionDate;
    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(name = "status")
    private String status;

    // ===== Getter & Setter =====

    public Integer getPetVaccinationId() { return petVaccinationId; }
    public void setPetVaccinationId(Integer petVaccinationId) { this.petVaccinationId = petVaccinationId; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public Vaccine getVaccine() { return vaccine; }
    public void setVaccine(Vaccine vaccine) { this.vaccine = vaccine; }

    public LocalDate getInjectionDate() { return injectionDate; }
    public void setInjectionDate(LocalDate injectionDate) { this.injectionDate = injectionDate; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}