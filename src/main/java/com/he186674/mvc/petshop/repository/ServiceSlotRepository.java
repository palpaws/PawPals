package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.ServiceSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServiceSlotRepository extends JpaRepository<ServiceSlot, Integer> {

    long countByService_ServiceId(Integer serviceId);

    List<ServiceSlot> findByService_ServiceIdAndSlotDateOrderByStartTimeAsc(Integer serviceId, LocalDate slotDate);

    @Query("SELECT s FROM ServiceSlot s " +
           "WHERE s.service.serviceId = :serviceId " +
           "AND s.slotDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.slotDate ASC, s.startTime ASC")
    List<ServiceSlot> findByServiceAndDateRange(
            @Param("serviceId") Integer serviceId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
