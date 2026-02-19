package com.example.expense.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.expense.model.Category;
import com.example.expense.model.Expense;
import com.example.expense.model.Role;
import com.example.expense.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BudgetServiceTest {

    private BudgetService budgetService;
    private User user;

    @BeforeEach
    void setUp() {
        budgetService = new BudgetService();
        user = new User(1L, "testuser", "password", Role.USER);
    }

    @Test
    void isBudgetExceeded_shouldReturnTrue_whenSpendingExceedsBudget() {
        user.setMonthlyBudget(new BigDecimal("500.00"));

        List<Expense> expenses = Arrays.asList(
                new Expense(new BigDecimal("200.00"), LocalDate.now(), "Grocery", user, new Category("Food")),
                new Expense(new BigDecimal("400.00"), LocalDate.now(), "Rent", user, new Category("Housing")));

        assertThat(budgetService.isBudgetExceeded(user, expenses)).isTrue();
    }

    @Test
    void isBudgetExceeded_shouldReturnFalse_whenSpendingWithinBudget() {
        user.setMonthlyBudget(new BigDecimal("1000.00"));

        List<Expense> expenses = Arrays.asList(
                new Expense(new BigDecimal("100.00"), LocalDate.now(), "Taxi", user, new Category("Transport")),
                new Expense(new BigDecimal("50.00"), LocalDate.now(), "Coffee", user, new Category("Food")));

        assertThat(budgetService.isBudgetExceeded(user, expenses)).isFalse();
    }

    @Test
    void isBudgetExceeded_shouldReturnFalse_whenSpendingEqualsBudget() {
        user.setMonthlyBudget(new BigDecimal("500.00"));
        List<Expense> expenses = List
                .of(new Expense(new BigDecimal("500.00"), LocalDate.now(), "Rent", user, new Category("Housing")));

        assertThat(budgetService.isBudgetExceeded(user, expenses)).isFalse();
    }

    @Test
    void isBudgetExceeded_shouldReturnFalse_whenBudgetNotSet() {
        user.setMonthlyBudget(null);
        List<Expense> expenses = List
                .of(new Expense(new BigDecimal("100.00"), LocalDate.now(), "Taxi", user, new Category("Transport")));

        assertThat(budgetService.isBudgetExceeded(user, expenses)).isFalse();
    }

    @Test
    void getRemainingBudget_shouldReturnCorrectValue() {
        user.setMonthlyBudget(new BigDecimal("1000.00"));
        List<Expense> expenses = List
                .of(new Expense(new BigDecimal("400.00"), LocalDate.now(), "Taxi", user, new Category("Transport")));

        assertThat(budgetService.getRemainingBudget(user, expenses)).isEqualByComparingTo("600.00");
    }

    @Test
    void getRemainingBudget_shouldReturnZero_whenBudgetNotSet() {
        user.setMonthlyBudget(null);
        List<Expense> expenses = List
                .of(new Expense(new BigDecimal("400.00"), LocalDate.now(), "Taxi", user, new Category("Transport")));

        assertThat(budgetService.getRemainingBudget(user, expenses)).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
