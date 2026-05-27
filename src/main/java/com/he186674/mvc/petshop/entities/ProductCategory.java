package com.he186674.mvc.petshop.entities;


import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "ProductCategories")
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    // ===== Getter & Setter =====

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}