package com.webshop.backend.service;

import com.webshop.backend.model.Category;
import com.webshop.backend.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        category.setCategoryId(null);
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public Optional<Category> getCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(categoryName);
    }

    public Category updateCategory(Integer categoryId, Category updatedCategory) {
        return categoryRepository.findById(categoryId).map(category -> {
            category.setCategoryName(updatedCategory.getCategoryName());
            category.setDescription(updatedCategory.getDescription());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
    }

    public void deleteCategory(Integer categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
