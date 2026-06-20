package com.he186674.mvc.petshop.repository;


import com.he186674.mvc.petshop.entities.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface VaccineRepository
        extends JpaRepository<Vaccine, Integer> {


    @Query("""
        SELECT v
        FROM Vaccine v
        WHERE v.species = :species
        OR v.species = 'Both'
    """)
    List<Vaccine> findAvailableVaccines(
            @Param("species") String species
    );

}