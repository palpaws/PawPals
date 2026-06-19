package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {

    @Query("SELECT p FROM BlogPost p WHERE p.status = 'PUBLISHED' AND (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY p.createdAt DESC")
    Page<BlogPost> findPublishedPosts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(l) FROM BlogLike l WHERE l.post.postId = :postId")
    Long countLikesByPostId(@Param("postId") Integer postId);

    @Query("SELECT COUNT(c) FROM BlogComment c WHERE c.post.postId = :postId")
    Long countCommentsByPostId(@Param("postId") Integer postId);
}