package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Integer> {
}