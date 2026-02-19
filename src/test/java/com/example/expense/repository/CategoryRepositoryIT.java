package com.example.expense.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.expense.model.Category;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoryRepositoryIT extends AbstractRepositoryIT {

    private CategoryRepository categoryRepository;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        categoryRepository = new CategoryRepository(entityManager);
    }

    @Test
    void saveAndFindByName() {
        Category category = new Category("Food");
        categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findByName("Food");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Food");
    }

    @Test
    void findByName_shouldReturnEmpty_whenNotFound() {
        Optional<Category> found = categoryRepository.findByName("NonExistent");
        assertThat(found).isEmpty();
    }
}
