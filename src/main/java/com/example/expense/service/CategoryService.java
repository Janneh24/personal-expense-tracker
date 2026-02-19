package com.example.expense.service;

import com.example.expense.model.Category;
import com.example.expense.repository.CategoryRepository;
import java.util.List;
import jakarta.inject.Inject;
import java.util.Arrays;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Inject
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category saveCategory(String name) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Category already exists");
        }
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.findById(id).ifPresent(categoryRepository::delete);
    }

    public void seedDefaultCategories() {
        List<String> defaults = Arrays.asList("Food", "Transport", "Rent", "Entertainment", "Health");
        for (String name : defaults) {
            if (categoryRepository.findByName(name).isEmpty()) {
                saveCategory(name);
            }
        }
    }
}
