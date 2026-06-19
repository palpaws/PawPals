package com.he186674.mvc.petshop.service.impl;

import com.he186674.mvc.petshop.dto.*;
import com.he186674.mvc.petshop.entities.*;
import com.he186674.mvc.petshop.repository.*;
import com.he186674.mvc.petshop.service.CommunityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommunityServiceImpl implements CommunityService {

    private final BlogPostRepository blogPostRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final BlogLikeRepository blogLikeRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final UserRepository userRepository;

    public CommunityServiceImpl(BlogPostRepository blogPostRepository,
                                BlogCommentRepository blogCommentRepository,
                                BlogLikeRepository blogLikeRepository,
                                BlogCategoryRepository blogCategoryRepository,
                                UserRepository userRepository) {
        this.blogPostRepository = blogPostRepository;
        this.blogCommentRepository = blogCommentRepository;
        this.blogLikeRepository = blogLikeRepository;
        this.blogCategoryRepository = blogCategoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<FeedDto> getFeed(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> posts = blogPostRepository.findPublishedPosts(keyword, pageable);

        return posts.map(post -> {
            FeedDto dto = new FeedDto();
            dto.setPostId(post.getPostId());
            dto.setTitle(post.getTitle());
            dto.setThumbnailUrl(post.getThumbnailUrl());
            dto.setAuthorName(post.getAuthor().getFullName());
            dto.setAuthorId(post.getAuthor().getUserId());
            dto.setCreatedAt(post.getCreatedAt());

            // Excerpt: first 200 chars of content
            String excerpt = post.getContent();
            if (excerpt != null && excerpt.length() > 200) {
                excerpt = excerpt.substring(0, 200) + "...";
            }
            dto.setExcerpt(excerpt);

            dto.setLikeCount(blogPostRepository.countLikesByPostId(post.getPostId()));
            dto.setCommentCount(blogPostRepository.countCommentsByPostId(post.getPostId()));

            return dto;
        });
    }

    @Override
    public PostDetailDto getPostDetail(Integer postId, Integer currentUserId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // Increment view count
        post.setViewCount(post.getViewCount() + 1);
        blogPostRepository.save(post);

        PostDetailDto dto = new PostDetailDto();
        dto.setPostId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setThumbnailUrl(post.getThumbnailUrl());
        dto.setContent(post.getContent());
        dto.setAuthorName(post.getAuthor().getFullName());
        dto.setAuthorId(post.getAuthor().getUserId());
        dto.setCategoryName(post.getCategory().getCategoryName());
        dto.setCategoryId(post.getCategory().getCategoryId());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        dto.setLikeCount(blogPostRepository.countLikesByPostId(postId));
        dto.setCommentCount(blogPostRepository.countCommentsByPostId(postId));

        if (currentUserId != null) {
            dto.setLikedByCurrentUser(blogLikeRepository.existsByPostPostIdAndUserUserId(postId, currentUserId));
        } else {
            dto.setLikedByCurrentUser(false);
        }

        return dto;
    }

    @Override
    public List<CommentTreeDto> getCommentTree(Integer postId) {
        List<BlogComment> rootComments = blogCommentRepository
                .findByPostPostIdAndParentCommentIsNullOrderByCreatedAtAsc(postId);

        return rootComments.stream()
                .map(this::buildCommentTree)
                .collect(Collectors.toList());
    }

    private CommentTreeDto buildCommentTree(BlogComment comment) {
        CommentTreeDto dto = new CommentTreeDto();
        dto.setCommentId(comment.getCommentId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setAuthorName(comment.getUser().getFullName());
        dto.setAuthorId(comment.getUser().getUserId());

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            dto.setReplies(comment.getReplies().stream()
                    .map(this::buildCommentTree)
                    .collect(Collectors.toList()));
        } else {
            dto.setReplies(new ArrayList<>());
        }

        return dto;
    }

    @Override
    public BlogPost createPost(CreatePostDto dto, Integer authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + authorId));

        BlogCategory category = blogCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoryId()));

        BlogPost post = new BlogPost();
        post.setTitle(dto.getTitle());
        post.setAuthor(author);
        post.setCategory(category);
        post.setThumbnailUrl(dto.getThumbnailUrl());
        post.setContent(dto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setStatus("PUBLISHED");
        post.setViewCount(0);

        // Generate slug from title
        String slug = dto.getTitle().toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("^-|-$", "");
        slug = slug + "-" + System.currentTimeMillis();
        post.setSlug(slug);

        return blogPostRepository.save(post);
    }

    @Override
    public BlogPost updatePost(Integer postId, CreatePostDto dto) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        BlogCategory category = blogCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        post.setTitle(dto.getTitle());
        post.setCategory(category);
        post.setThumbnailUrl(dto.getThumbnailUrl());
        post.setContent(dto.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return blogPostRepository.save(post);
    }

    @Override
    public void deletePost(Integer postId, Integer currentUserId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // Check if current user is author or ADMIN
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!post.getAuthor().getUserId().equals(currentUserId) && !"ADMIN".equals(currentUser.getRole())) {
            throw new RuntimeException("You are not authorized to delete this post");
        }

        blogPostRepository.delete(post);
    }

    @Override
    public boolean toggleLike(Integer postId, Integer userId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var existingLike = blogLikeRepository.findByPostPostIdAndUserUserId(postId, userId);

        if (existingLike.isPresent()) {
            // Unlike
            blogLikeRepository.delete(existingLike.get());
            return false; // false = unliked
        } else {
            // Like
            BlogLike like = new BlogLike();
            like.setId(new BlogLikeId(postId, userId));
            like.setPost(post);
            like.setUser(user);
            like.setLikedAt(LocalDateTime.now());
            blogLikeRepository.save(like);
            return true; // true = liked
        }
    }

    @Override
    public void addComment(Integer postId, Integer userId, String content) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BlogComment comment = new BlogComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        blogCommentRepository.save(comment);
    }

    @Override
    public void addReply(Integer commentId, Integer userId, String content) {
        BlogComment parentComment = blogCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BlogComment reply = new BlogComment();
        reply.setPost(parentComment.getPost());
        reply.setUser(user);
        reply.setContent(content);
        reply.setParentComment(parentComment);
        reply.setCreatedAt(LocalDateTime.now());

        blogCommentRepository.save(reply);
    }
}