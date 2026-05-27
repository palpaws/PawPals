package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BlogComments")
public class BlogComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "PostId")
    private BlogPost post;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    @Column(nullable = false)
    private String content;

    private LocalDateTime createdAt;

    // ===== Getter & Setter =====

    public Integer getCommentId() { return commentId; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }

    public BlogPost getPost() { return post; }
    public void setPost(BlogPost post) { this.post = post; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}