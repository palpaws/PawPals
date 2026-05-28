package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BlogLikeId implements Serializable {

    @Column(name = "post_id")
    private Integer postId;
    @Column(name = "user_id")
    private Integer userId;

    public BlogLikeId() {}

    public BlogLikeId(Integer postId, Integer userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlogLikeId)) return false;
        BlogLikeId that = (BlogLikeId) o;
        return Objects.equals(postId, that.postId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }
}