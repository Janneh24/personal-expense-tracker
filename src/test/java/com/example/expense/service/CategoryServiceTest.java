package com.example.expense.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.example.expense.model.Category;
import com.example.expense.repository.CategoryRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    @Test
    void getAllCategories_shouldReturnList() {
        List<Category> categories = Arrays.asList(new Category("Food"), new Category("Transport"));
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2).containsAll(categories);
        verify(categoryRepository).findAll();
    }

    @Test
    void saveCategory_shouldSave_whenNameIsUnique() {
        String name = "New Category";
        when(categoryRepository.findByName(name)).thenReturn(Optional.empty());
        Category saved = new Category(name);
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        Category result = categoryService.saveCategory(name);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);

        verify(categoryRepository).save(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue().getName()).isEqualTo(name);
    }

    @Test
    void saveCategory_shouldThrow_whenNameAlreadyExists() {
        String name = "Existing";
        when(categoryRepository.findByName(name)).thenReturn(Optional.of(new Category(name)));

        assertThatThrownBy(() -> categoryService.saveCategory(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category already exists");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_shouldCallRepository_whenExists() {
        Long id = 1L;
        Category category = new Category("Food");
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(id);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_shouldDoNothing_whenNotExists() {
        Long id = 1L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        categoryService.deleteCategory(id);

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void seedDefaultCategories_shouldSaveOnlyMissing() {
        when(categoryRepository.findByName("Food")).thenReturn(Optional.of(new Category("Food")));
        when(categoryRepository.findByName("Transport")).thenReturn(Optional.empty());
        when(categoryRepository.findByName("Rent")).thenReturn(Optional.empty());
        when(categoryRepository.findByName("Entertainment")).thenReturn(Optional.empty());
        when(categoryRepository.findByName("Health")).thenReturn(Optional.empty());

        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        categoryService.seedDefaultCategories();

        verify(categoryRepository, times(4)).save(any(Category.class));
        verify(categoryRepository).findByName("Food");
    }
}
