package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "BlogCategories")
public class BlogCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(unique = true, nullable = false)
    private String categoryName;

    private String description;

    @OneToMany(mappedBy = "category")
    private List<BlogPost> posts;

    // ===== Getter & Setter =====

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<BlogPost> getPosts() { return posts; }
    public void setPosts(List<BlogPost> posts) { this.posts = posts; }
}