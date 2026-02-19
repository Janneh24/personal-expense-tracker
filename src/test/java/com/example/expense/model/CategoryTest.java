package com.example.expense.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void testCategoryProperties() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setSystem(true);

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Food");
        assertThat(category.isSystem()).isTrue();
    }

    @Test
    void testConstructor() {
        Category category = new Category("Transport");
        assertThat(category.getName()).isEqualTo("Transport");
        assertThat(category.isSystem()).isFalse();
    }

    @Test
    void testToString() {
        Category category = new Category("Entertainment");
        category.setId(5L);
        assertThat(category.toString()).contains("Entertainment");
        assertThat(category.toString()).contains("5");
    }
}
