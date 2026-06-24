package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {

    @Query("SELECT p FROM BlogPost p WHERE p.status = 'PUBLISHED' AND (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY p.createdAt DESC")
    Page<BlogPost> findPublishedPosts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM BlogPost p WHERE p.status = 'DRAFT' ORDER BY p.createdAt DESC")
    Page<BlogPost> findDraftPosts(Pageable pageable);

    @Query("SELECT p FROM BlogPost p WHERE p.author.userId = :authorId ORDER BY p.createdAt DESC")
    List<BlogPost> findByAuthorId(@Param("authorId") Integer authorId);

    @Query("SELECT COUNT(l) FROM BlogLike l WHERE l.post.postId = :postId")
    Long countLikesByPostId(@Param("postId") Integer postId);

    @Query("SELECT COUNT(c) FROM BlogComment c WHERE c.post.postId = :postId")
    Long countCommentsByPostId(@Param("postId") Integer postId);
}
