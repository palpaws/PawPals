package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.Pet;
import com.he186674.mvc.petshop.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository
        extends JpaRepository<Pet, Integer> {

    List<Pet> findByUser(User user);

}