package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Payment findByOrderId(String orderId);
}