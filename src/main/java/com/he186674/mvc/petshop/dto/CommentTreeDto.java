package com.he186674.mvc.petshop.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentTreeDto {
    private Integer commentId;
    private String content;
    private LocalDateTime createdAt;
    private String authorName;
    private Integer authorId;
    private List<CommentTreeDto> replies;

    public Integer getCommentId() { return commentId; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public Integer getAuthorId() { return authorId; }
    public void setAuthorId(Integer authorId) { this.authorId = authorId; }

    public List<CommentTreeDto> getReplies() { return replies; }
    public void setReplies(List<CommentTreeDto> replies) { this.replies = replies; }
}