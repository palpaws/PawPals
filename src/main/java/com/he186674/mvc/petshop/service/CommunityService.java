package com.he186674.mvc.petshop.service;

import com.he186674.mvc.petshop.dto.*;
import com.he186674.mvc.petshop.entities.BlogPost;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommunityService {

    Page<FeedDto> getFeed(String keyword, int page, int size);

    List<BlogPost> getPostsByAuthor(Integer authorId);

    Page<BlogPost> getDraftPosts(int page, int size);

    void approvePost(Integer postId);

    void rejectPost(Integer postId);

    PostDetailDto getPostDetail(Integer postId, Integer currentUserId);

    List<CommentTreeDto> getCommentTree(Integer postId);

    BlogPost createPost(CreatePostDto dto, Integer authorId);

    BlogPost updatePost(Integer postId, CreatePostDto dto);

    void deletePost(Integer postId, Integer currentUserId);

    boolean toggleLike(Integer postId, Integer userId);

    void addComment(Integer postId, Integer userId, String content);

    void addReply(Integer commentId, Integer userId, String content);
}