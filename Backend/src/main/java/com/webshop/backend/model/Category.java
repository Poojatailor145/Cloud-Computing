package com.webshop.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Ensures auto-increment if supported
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

