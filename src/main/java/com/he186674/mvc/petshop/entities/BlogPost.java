package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "BlogPosts")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @ManyToOne
    @JoinColumn(name = "AuthorId")
    private User author;

    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private BlogCategory category;

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String slug;

    private String thumbnailUrl;

    @Lob
    private String content;

    private Integer viewCount = 0;

    private LocalDateTime createdAt;

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<BlogComment> getComments() { return comments; }
    public void setComments(List<BlogComment> comments) { this.comments = comments; }

    public List<BlogLike> getLikes() { return likes; }
    public void setLikes(List<BlogLike> likes) { this.likes = likes; }
}