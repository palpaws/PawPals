package com.he186674.mvc.petshop.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "blog_posts")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private BlogCategory category;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "content")
    @Lob
    private String content;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<BlogComment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<BlogLike> likes;

    // ===== Getter & Setter =====

    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public BlogCategory getCategory() { return category; }
    public void setCategory(BlogCategory category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<BlogComment> getComments() { return comments; }
    public void setComments(List<BlogComment> comments) { this.comments = comments; }

    public List<BlogLike> getLikes() { return likes; }
    public void setLikes(List<BlogLike> likes) { this.likes = likes; }
}