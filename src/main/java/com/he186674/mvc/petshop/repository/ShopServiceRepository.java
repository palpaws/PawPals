package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.ShopService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopServiceRepository extends JpaRepository<ShopService, Integer> {

    List<ShopService> findByIsActiveTrue();

    List<ShopService> findByServiceTypeAndIsActiveTrue(String serviceType);

    List<ShopService> findByShop_ShopIdAndIsActiveTrue(Integer shopId);

    List<ShopService> findByShop_ShopIdAndServiceTypeAndIsActiveTrue(Integer shopId, String serviceType);
}
