package com.he186674.mvc.petshop.repository;


import com.he186674.mvc.petshop.entities.PetVaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PetVaccinationRepository
        extends JpaRepository<PetVaccination,Integer>{

}