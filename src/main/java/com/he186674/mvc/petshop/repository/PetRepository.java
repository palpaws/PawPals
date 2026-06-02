package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {
    List<Pet> findByUser_UserId(Integer userId);
}