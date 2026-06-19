package com.he186674.mvc.petshop.repository;

import com.he186674.mvc.petshop.entities.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Integer> {

    List<BlogComment> findByPostPostIdAndParentCommentIsNullOrderByCreatedAtAsc(Integer postId);
}