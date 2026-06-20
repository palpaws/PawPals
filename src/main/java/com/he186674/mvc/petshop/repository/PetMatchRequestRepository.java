package com.he186674.mvc.petshop.repository;


import com.he186674.mvc.petshop.entities.PetMatchRequest;
import com.he186674.mvc.petshop.entities.Pet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface PetMatchRequestRepository
        extends JpaRepository<PetMatchRequest,Integer> {



    // kiểm tra đã gửi request chưa
    boolean existsBySenderPetAndReceiverPet(
            Pet senderPet,
            Pet receiverPet
    );



    // danh sách request mình gửi
    List<PetMatchRequest>
    findBySenderPet(Pet senderPet);



    // danh sách request người khác gửi tới pet của mình
    List<PetMatchRequest>
    findByReceiverPet(Pet receiverPet);



    // lọc theo trạng thái
    List<PetMatchRequest>
    findByReceiverPetAndStatus(
            Pet receiverPet,
            String status
    );

    List<PetMatchRequest>
    findByReceiverPet_User_UserId(
            Integer userId
    );

    List<PetMatchRequest> findByReceiverPet_User_UserIdAndStatus(
            Integer userId,
            String status
    );



}