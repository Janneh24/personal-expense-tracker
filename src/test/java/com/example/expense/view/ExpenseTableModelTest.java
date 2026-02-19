package com.example.expense.view;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.expense.model.Category;
import com.example.expense.model.Expense;
import com.example.expense.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExpenseTableModelTest {

    private ExpenseTableModel model;
    private User user;
    private Category category;
    private Expense expense1;
    private Expense expense2;

    @BeforeEach
    void setUp() {
        model = new ExpenseTableModel();
        user = new User(1L, "test", "pass", com.example.expense.model.Role.USER);
        category = new Category("Food");
        expense1 = new Expense(new BigDecimal("50.00"), LocalDate.of(2023, 6, 15), "Lunch", user, category);
        expense2 = new Expense(new BigDecimal("20.00"), LocalDate.of(2023, 6, 16), "Coffee", user, null);
    }

    @Test
    void getRowCount_shouldReturnZeroForEmpty() {
        assertThat(model.getRowCount()).isZero();
    }

    @Test
    void getRowCount_shouldReturnCorrectCount() {
        model.setExpenses(Arrays.asList(expense1, expense2));
        assertThat(model.getRowCount()).isEqualTo(2);
    }

    @Test
    void getColumnCount_shouldReturnFour() {
        assertThat(model.getColumnCount()).isEqualTo(4);
    }

    @Test
    void getColumnName_shouldReturnCorrectNames() {
        assertThat(model.getColumnName(0)).isEqualTo("Date");
        assertThat(model.getColumnName(1)).isEqualTo("Category");
        assertThat(model.getColumnName(2)).isEqualTo("Description");
        assertThat(model.getColumnName(3)).isEqualTo("Amount");
    }

    @Test
    void getValueAt_shouldReturnDate() {
        model.setExpenses(List.of(expense1));
        assertThat(model.getValueAt(0, 0)).isEqualTo(LocalDate.of(2023, 6, 15));
    }

    @Test
    void getValueAt_shouldReturnCategoryName() {
        model.setExpenses(List.of(expense1));
        assertThat(model.getValueAt(0, 1)).isEqualTo("Food");
    }

    @Test
    void getValueAt_shouldReturnNAForNullCategory() {
        model.setExpenses(List.of(expense2));
        assertThat(model.getValueAt(0, 1)).isEqualTo("N/A");
    }

    @Test
    void getValueAt_shouldReturnDescription() {
        model.setExpenses(List.of(expense1));
        assertThat(model.getValueAt(0, 2)).isEqualTo("Lunch");
    }

    @Test
    void getValueAt_shouldReturnAmount() {
        model.setExpenses(List.of(expense1));
        assertThat(model.getValueAt(0, 3)).isEqualTo(new BigDecimal("50.00"));
    }

    @Test
    void getValueAt_shouldReturnNullForInvalidColumn() {
        model.setExpenses(List.of(expense1));
        assertThat(model.getValueAt(0, 99)).isNull();
    }

    @Test
    void setExpenses_shouldClearAndReplace() {
        model.setExpenses(List.of(expense1));
        assertThat(model.getRowCount()).isEqualTo(1);

        model.setExpenses(Arrays.asList(expense1, expense2));
        assertThat(model.getRowCount()).isEqualTo(2);
    }

    @Test
    void getExpenses_shouldReturnDefensiveCopy() {
        model.setExpenses(List.of(expense1));
        List<Expense> copy = model.getExpenses();
        copy.clear();
        assertThat(model.getRowCount()).isEqualTo(1); // Original list unaffected
    }

    @Test
    void getExpenseAt_shouldReturnCorrectExpense() {
        model.setExpenses(Arrays.asList(expense1, expense2));
        assertThat(model.getExpenseAt(0)).isEqualTo(expense1);
        assertThat(model.getExpenseAt(1)).isEqualTo(expense2);
    }
}
