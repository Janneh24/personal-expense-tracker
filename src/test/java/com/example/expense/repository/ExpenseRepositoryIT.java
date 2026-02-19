package com.example.expense.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.expense.model.Category;
import com.example.expense.model.Expense;
import com.example.expense.model.Role;
import com.example.expense.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExpenseRepositoryIT extends AbstractRepositoryIT {

    private ExpenseRepository expenseRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private User user;
    private Category category;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        expenseRepository = new ExpenseRepository(entityManager);
        userRepository = new UserRepository(entityManager);
        categoryRepository = new CategoryRepository(entityManager);

        user = new User(null, "testuser", "password", Role.USER);
        userRepository.save(user);

        category = new Category("Food");
        categoryRepository.save(category);
    }

    @Test
    void saveAndFindByUser() {
        Expense expense = new Expense(new BigDecimal("100.00"), LocalDate.now(), "Grocery", user, category);
        expenseRepository.save(expense);

        List<Expense> found = expenseRepository.findByUser(user);
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDescription()).isEqualTo("Grocery");
    }

    @Test
    void findByUserAndDateBetween() {
        LocalDate today = LocalDate.now();
        Expense expense1 = new Expense(new BigDecimal("10.00"), today.minusDays(5), "Past", user, category);
        Expense expense2 = new Expense(new BigDecimal("20.00"), today, "Today", user, category);
        Expense expense3 = new Expense(new BigDecimal("30.00"), today.plusDays(5), "Future", user, category);

        expenseRepository.save(expense1);
        expenseRepository.save(expense2);
        expenseRepository.save(expense3);

        List<Expense> found = expenseRepository.findByUserAndDateBetween(user, today.minusDays(1), today.plusDays(1));
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDescription()).isEqualTo("Today");
    }
}
