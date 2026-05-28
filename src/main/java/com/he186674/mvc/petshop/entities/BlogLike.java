package com.he186674.mvc.petshop.entities;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blog_likes")
public class BlogLike {

    @EmbeddedId
    private BlogLikeId id;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private BlogPost post;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "liked_at")
    private LocalDateTime likedAt;

    // ===== Getter & Setter =====

    public BlogLikeId getId() { return id; }
    public void setId(BlogLikeId id) { this.id = id; }

    public BlogPost getPost() { return post; }
    public void setPost(BlogPost post) { this.post = post; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getLikedAt() { return likedAt; }
    public void setLikedAt(LocalDateTime likedAt) { this.likedAt = likedAt; }
}