package com.example.expense.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ExpenseTest {

    @Test
    void testExpenseProperties() {
        Expense expense = new Expense();
        User user = new User();
        Category category = new Category();
        LocalDate now = LocalDate.now();
        BigDecimal amount = new BigDecimal("50.00");

        expense.setId(10L);
        expense.setAmount(amount);
        expense.setDate(now);
        expense.setDescription("Pizza");
        expense.setUser(user);
        expense.setCategory(category);

        assertThat(expense.getId()).isEqualTo(10L);
        assertThat(expense.getAmount()).isEqualTo(amount);
        assertThat(expense.getDate()).isEqualTo(now);
        assertThat(expense.getDescription()).isEqualTo("Pizza");
        assertThat(expense.getUser()).isEqualTo(user);
        assertThat(expense.getCategory()).isEqualTo(category);
    }

    @Test
    void testConstructor() {
        User user = new User();
        Category category = new Category("Food");
        LocalDate now = LocalDate.now();
        BigDecimal amount = new BigDecimal("25.00");

        Expense expense = new Expense(amount, now, "Lunch", user, category);
        assertThat(expense.getAmount()).isEqualTo(amount);
        assertThat(expense.getDate()).isEqualTo(now);
        assertThat(expense.getDescription()).isEqualTo("Lunch");
        assertThat(expense.getUser()).isEqualTo(user);
        assertThat(expense.getCategory()).isEqualTo(category);
    }

    @Test
    void testToString() {
        Expense expense = new Expense(new BigDecimal("10.00"), LocalDate.now(), "Coffee", null, null);
        expense.setId(1L);
        assertThat(expense.toString()).contains("Coffee");
        assertThat(expense.toString()).contains("10.00");
    }
}
