package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.ServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Integer> {

    List<ServiceBooking> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);

    boolean existsByUser_UserIdAndSlot_SlotIdAndStatusNot(Integer userId, Integer slotId, String status);
}
