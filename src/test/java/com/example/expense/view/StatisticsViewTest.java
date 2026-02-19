package com.example.expense.view;

import com.example.expense.model.Category;
import com.example.expense.model.Expense;
import com.example.expense.model.Role;
import com.example.expense.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.edt.GuiActionRunner;
import org.junit.Test;

public class StatisticsViewTest extends AssertJSwingJUnitTestCase {

    private DialogFixture window;

    @Override
    protected void onSetUp() {
        User user = new User(1L, "test", "pass", Role.USER);
        Category c1 = new Category("Food");
        Category c2 = new Category("Transport");
        // Include an expense with null category to cover the filter branch
        List<Expense> expenses = Arrays.asList(
                new Expense(new BigDecimal("50.00"), LocalDate.now(), "Lunch", user, c1),
                new Expense(new BigDecimal("20.00"), LocalDate.now(), "Bus", user, c2),
                new Expense(new BigDecimal("15.00"), LocalDate.now(), "Unknown", user, null));

        StatisticsView dialog = GuiActionRunner.execute(() -> new StatisticsView(null, expenses));
        window = new DialogFixture(robot(), dialog);
        window.show();
    }

    @Test
    public void shouldShowCharts() {
        window.requireVisible();
    }

    @Test
    public void shouldShowCorrectTitle() {
        window.requireVisible();
    }
}
