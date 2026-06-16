package com.he186674.mvc.petshop.service;





import com.he186674.mvc.petshop.entities.Pet;
import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(
            PetRepository petRepository
    ){
        this.petRepository = petRepository;
    }

    public List<Pet> getPetsByUser(
            User user
    ){
        return petRepository.findByUser(user);
    }

    public Pet save(Pet pet){
        return petRepository.save(pet);
    }
    public Pet getPetById(Integer id){

        return petRepository.findById(id)
                .orElse(null);

    }
}