package com.he186674.mvc.petshop.controller;

import com.he186674.mvc.petshop.entities.Pet;
import com.he186674.mvc.petshop.entities.PetImage;
import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.repository.PetImageRepository;
import com.he186674.mvc.petshop.service.PetService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.UUID;

@Controller
public class PetController {

    private final PetService petService;
    private final PetImageRepository petImageRepository;

    public PetController(
            PetService petService,
            PetImageRepository petImageRepository
    ){
        this.petService = petService;
        this.petImageRepository = petImageRepository;
    }

    @GetMapping("/pets")
    public String petsPage(
            HttpSession session,
            Model model
    ){

        User currentUser =
                (User) session.getAttribute(
                        "currentUser"
                );

        if(currentUser == null){
            return "redirect:/login";
        }

        List<Pet> pets =
                petService.getPetsByUser(
                        currentUser
                );

        model.addAttribute(
                "pets",
                pets
        );

        return "MyPets";
    }

    @PostMapping("/pets/create")
    public String createPet(

            @RequestParam String petName,
            @RequestParam String species,
            @RequestParam String gender,

            @RequestParam("imageFile")
            MultipartFile imageFile,

            HttpSession session

    ) throws Exception {

        User currentUser =
                (User) session.getAttribute(
                        "currentUser"
                );

        if(currentUser == null){
            return "redirect:/login";
        }

        /*
         * Lưu PET
         */

        Pet pet = new Pet();

        pet.setPetName(
                petName
        );

        pet.setSpecies(
                species
        );

        pet.setGender(
                gender
        );

        pet.setUser(
                currentUser
        );

        pet.setStatus(
                "ACTIVE"
        );

        pet.setVaccinated(
                false
        );

        pet.setSterilized(
                false
        );

        pet.setMicrochipCode(
                UUID.randomUUID().toString()
        );
        Pet savedPet =
                petService.save(
                        pet
                );

        /*
         * Lưu ẢNH
         */

        if(!imageFile.isEmpty()){

            String fileName =
                    UUID.randomUUID()
                            + "_"
                            + imageFile.getOriginalFilename();

            // Get upload directory from application.properties, fallback to project-relative path
            String uploadDir = "uploads/pets";
            Path uploadPath = Paths.get(uploadDir);

            // Try absolute path relative to project
            if (!Files.exists(uploadPath)) {
                try {
                    uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "pets");
                } catch (Exception e) {
                    uploadPath = Paths.get("uploads/pets");
                }
            }

            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            Files.copy(
                    imageFile.getInputStream(),
                    uploadPath.resolve(fileName)
            );

            PetImage petImage =
                    new PetImage();

            petImage.setPet(
                    savedPet
            );

            petImage.setImageUrl(
                    "/uploads/pets/" + fileName
            );

            petImage.setIsPrimary(
                    true
            );

            petImageRepository.save(
                    petImage
            );
        }

        return "redirect:/pets";
    }

}