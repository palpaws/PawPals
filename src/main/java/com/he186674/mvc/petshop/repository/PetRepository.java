package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.entities.Pet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {


    List<Pet> findByUser_UserId(Integer userId);


    List<Pet> findByUser(User user);


    @Query("""
        SELECT DISTINCT p
        FROM Pet p
        LEFT JOIN FETCH p.vaccinations pv
        LEFT JOIN FETCH pv.vaccine
        WHERE p.petId = :id
    """)
    Pet findPetDetail(@Param("id") Integer id);

    List<Pet> findByUserNot(User user);

    List<Pet> findByIsAvailableForMatchingTrue();

    @Query(value = """
    SELECT TOP 1 *
    FROM pets
    WHERE user_id <> :userId
    AND is_available_for_matching = 1
    AND status = 'ACTIVE'
    ORDER BY NEWID()
    """,
            nativeQuery = true)
    Pet findRandomAvailablePet(
            @Param("userId") Integer userId
    );

}