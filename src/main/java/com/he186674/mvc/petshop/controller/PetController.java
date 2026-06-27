package com.he186674.mvc.petshop.controller;


import com.he186674.mvc.petshop.entities.Pet;
import com.he186674.mvc.petshop.entities.PetImage;
import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.entities.Vaccine;
import com.he186674.mvc.petshop.entities.PetVaccination;

import com.he186674.mvc.petshop.repository.PetImageRepository;
import com.he186674.mvc.petshop.repository.PetMatchRequestRepository;
import com.he186674.mvc.petshop.repository.VaccineRepository;
import com.he186674.mvc.petshop.repository.PetVaccinationRepository;

import com.he186674.mvc.petshop.service.PetService;

import com.he186674.mvc.petshop.entities.PetMatchRequest;

import java.time.LocalDateTime;


import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;



@Controller
public class PetController {


    private final PetService petService;

    private final PetImageRepository petImageRepository;

    private final VaccineRepository vaccineRepository;

    private final PetVaccinationRepository petVaccinationRepository;

    private final PetMatchRequestRepository petMatchRequestRepository;



    public PetController(

            PetService petService,

            PetImageRepository petImageRepository,

            VaccineRepository vaccineRepository,

            PetMatchRequestRepository petMatchRequestRepository,

            PetVaccinationRepository petVaccinationRepository

    ){

        this.petService = petService;

        this.petImageRepository = petImageRepository;

        this.vaccineRepository = vaccineRepository;

        this.petMatchRequestRepository =
                petMatchRequestRepository;

        this.petVaccinationRepository =
                petVaccinationRepository;

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






    @GetMapping("/pets/{id}")
    public String petDetail(

            @PathVariable("id") Integer id,

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




        Pet pet =
                petService.getPetById(id);




        if(pet == null){

            return "redirect:/pets";

        }




        model.addAttribute(
                "pet",
                pet
        );




        // Lấy danh sách vaccine phù hợp với chó/mèo

        List<Vaccine> vaccines =
                vaccineRepository.findAvailableVaccines(
                        pet.getSpecies()
                );



        model.addAttribute(
                "vaccines",
                vaccines
        );




        return "PetDetail";

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

        pet.setIsAvailableForMatching(true);


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







        if(!imageFile.isEmpty()){



            String fileName =

                    UUID.randomUUID()
                            + "_"
                            + imageFile.getOriginalFilename();




            Path uploadPath = Paths.get(
                    "src/main/resources/static/uploads/pets"
            );



            if(!Files.exists(uploadPath)){

                Files.createDirectories(
                        uploadPath
                );

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









    @PostMapping("/pets/{id}/update")
    public String updatePet(


            @PathVariable("id") Integer id,


            @RequestParam String petName,


            @RequestParam String breed,


            @RequestParam String color,


            @RequestParam(required = false) Double weight,

            @RequestParam(required = false) Double height,


            @RequestParam String healthStatus,


            @RequestParam String temperament,


            @RequestParam String activityLevel,

            @RequestParam(required = false) String bloodType,

            @RequestParam(required = false) LocalDate dateOfBirth,

            @RequestParam(required = false) LocalDate birthday,

            @RequestParam(required = false) LocalDate lastVetVisit



    ){



        Pet pet =
                petService.getPetById(id);




        if(pet != null){



            pet.setPetName(
                    petName
            );



            pet.setBreed(
                    breed
            );

            pet.setBloodType(
                    bloodType
            );


            pet.setDateOfBirth(
                    dateOfBirth
            );


            pet.setBirthday(
                    birthday
            );


            pet.setLastVetVisit(
                    lastVetVisit
            );



            pet.setColor(
                    color
            );



            pet.setWeight(
                    weight
            );



            pet.setHeight(
                    height
            );



            pet.setHealthStatus(
                    healthStatus
            );



            pet.setTemperament(
                    temperament
            );



            pet.setActivityLevel(
                    activityLevel
            );




            petService.save(
                    pet
            );


        }





        return "redirect:/pets/" + id;


    }









    @PostMapping("/pets/{id}/vaccination")
    public String addVaccination(

            @PathVariable Integer id,

            @RequestParam Integer vaccineId,

            @RequestParam LocalDate injectionDate,

            @RequestParam(required = false)
            LocalDate nextDueDate

    ){

        Pet pet = petService.getPetById(id);

        if(pet == null){
            return "redirect:/pets";
        }


        Vaccine vaccine =
                vaccineRepository.findById(vaccineId)
                        .orElseThrow();


        PetVaccination vaccination =
                new PetVaccination();


        vaccination.setPet(pet);

        vaccination.setVaccine(vaccine);

        vaccination.setInjectionDate(injectionDate);

        vaccination.setNextDueDate(nextDueDate);

        vaccination.setStatus("COMPLETED");


        petVaccinationRepository.save(vaccination);


        return "redirect:/pets/" + id;
    }

    @GetMapping("/pet-match")
    public String petMatchPage(

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



        // lấy 1 pet ngẫu nhiên của người khác

        Pet pet =
                petService.getRandomAvailablePet(
                        currentUser
                );



        // pet của mình để chọn trong modal

        List<Pet> myPets =
                petService.getPetsByUser(
                        currentUser
                );



        model.addAttribute(
                "pet",
                pet
        );


        model.addAttribute(
                "myPets",
                myPets
        );


        return "pet-match";

    }

    @GetMapping("/pet-match/next")
    @ResponseBody
    public Pet nextPet(
            HttpSession session
    ){

        User currentUser =
                (User) session.getAttribute("currentUser");


        if(currentUser == null){

            return null;

        }


        return petService.getRandomAvailablePet(
                currentUser
        );

    }



    @PostMapping("/pet-match/request")
    public String sendMatchRequest(

            @RequestParam Integer senderPetId,

            @RequestParam Integer receiverPetId,

            HttpSession session

    ){


        User currentUser =
                (User) session.getAttribute("currentUser");



        if(currentUser == null){

            return "redirect:/login";

        }



        Pet senderPet =
                petService.getPetById(
                        senderPetId
                );



        Pet receiverPet =
                petService.getPetById(
                        receiverPetId
                );




        if(senderPet == null ||
                receiverPet == null){

            return "redirect:/pet-match";

        }




        PetMatchRequest request =
                new PetMatchRequest();



        request.setSenderPet(
                senderPet
        );


        request.setReceiverPet(
                receiverPet
        );



        request.setMessage(
                "Xin chào, mình muốn cho thú cưng làm quen!"
        );



        request.setStatus(
                "PENDING"
        );



        request.setCreatedAt(
                LocalDateTime.now()
        );




        petMatchRequestRepository.save(
                request
        );



        return "redirect:/pet-match";

    }

    @GetMapping("/pet-match/request")
    public String matchRequests(

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



        List<PetMatchRequest> requests =
                petMatchRequestRepository
                        .findByReceiverPet_User_UserIdAndStatus(
                                currentUser.getUserId(),
                                "PENDING"
                        );


        model.addAttribute(
                "requests",
                requests
        );



        return "pet-match-request";

    }

    @GetMapping("/pet-match/matches")
    public String matchPage(HttpSession session, Model model) {

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        List<PetMatchRequest> matches =
                petMatchRequestRepository
                        .findByReceiverPet_User_UserIdAndStatus(
                                currentUser.getUserId(),
                                "ACCEPTED"
                        );

        model.addAttribute("matches", matches);

        return "pet-match-success";
    }

    @PostMapping("/pet-match/request/{id}/accept")
    public String acceptRequest(

            @PathVariable Integer id

    ){


        PetMatchRequest request =
                petMatchRequestRepository
                        .findById(id)
                        .orElseThrow();



        request.setStatus(
                "ACCEPTED"
        );


        petMatchRequestRepository.save(
                request
        );


        return "redirect:/pet-match/request";

    }

    @PostMapping("/pet-match/request/{id}/reject")
    public String rejectRequest(

            @PathVariable Integer id

    ){


        PetMatchRequest request =
                petMatchRequestRepository
                        .findById(id)
                        .orElseThrow();



        request.setStatus(
                "REJECTED"
        );


        petMatchRequestRepository.save(
                request
        );



        return "redirect:/pet-match/request";

    }
}