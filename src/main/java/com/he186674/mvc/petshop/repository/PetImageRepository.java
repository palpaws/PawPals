package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.PetImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetImageRepository
        extends JpaRepository<PetImage,Integer> {
}