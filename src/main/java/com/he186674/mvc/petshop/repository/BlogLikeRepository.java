package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.BlogLike;
import com.he186674.mvc.petshop.entities.BlogLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogLikeRepository extends JpaRepository<BlogLike, BlogLikeId> {

    Optional<BlogLike> findByPostPostIdAndUserUserId(Integer postId, Integer userId);

    boolean existsByPostPostIdAndUserUserId(Integer postId, Integer userId);

    Long countByPostPostId(Integer postId);
}