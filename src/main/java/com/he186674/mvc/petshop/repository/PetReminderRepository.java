package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.PetReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PetReminderRepository extends JpaRepository<PetReminder, Integer> {

    List<PetReminder> findByUser_UserIdAndEventDateBetween(
            Integer userId, LocalDate startDate, LocalDate endDate);

    List<PetReminder> findByUser_UserIdAndPet_PetIdAndEventDateBetween(
            Integer userId, Integer petId, LocalDate startDate, LocalDate endDate);

    List<PetReminder> findByUser_UserIdAndIsCompletedFalseOrderByEventDateAsc(Integer userId);

    List<PetReminder> findByUser_UserIdAndPet_PetIdAndIsCompletedFalseOrderByEventDateAsc(
            Integer userId, Integer petId);

    @Query("SELECT pr FROM PetReminder pr WHERE pr.user.userId = :userId " +
           "AND pr.eventDate BETWEEN :startDate AND :endDate " +
           "AND (:petId IS NULL OR pr.pet.petId = :petId)")
    List<PetReminder> findByUserAndDateRange(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("petId") Integer petId);
}