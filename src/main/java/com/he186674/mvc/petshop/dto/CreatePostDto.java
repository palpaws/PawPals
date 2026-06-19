package com.he186674.mvc.petshop.dto;

public class CreatePostDto {
    private String title;
    private Integer categoryId;
    private String thumbnailUrl;
    private String content;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}