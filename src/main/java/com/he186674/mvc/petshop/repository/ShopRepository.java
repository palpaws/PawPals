package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {
    List<Shop> findByIsActiveTrue();
}
