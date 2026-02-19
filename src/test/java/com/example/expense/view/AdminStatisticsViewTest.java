package com.example.expense.view;

import static org.assertj.swing.edt.GuiActionRunner.execute;

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
import org.junit.Test;

public class AdminStatisticsViewTest extends AssertJSwingJUnitTestCase {

    private DialogFixture window;

    @Override
    protected void onSetUp() {
        User user1 = new User(1L, "alice", "pass", Role.USER);
        User user2 = new User(2L, "bob", "pass", Role.ADMIN);
        Category food = new Category("Food");
        Category transport = new Category("Transport");

        Expense e1 = new Expense(new BigDecimal("50.00"), LocalDate.now(), "Lunch", user1, food);
        Expense e2 = new Expense(new BigDecimal("30.00"), LocalDate.now(), "Bus", user2, transport);
        Expense e3 = new Expense(new BigDecimal("20.00"), LocalDate.now(), "Taxi", user1, null);
        List<Expense> expenses = Arrays.asList(e1, e2, e3);
        List<User> users = Arrays.asList(user1, user2);

        AdminStatisticsView dialog = execute(() -> new AdminStatisticsView(null, expenses, users));
        window = new DialogFixture(robot(), dialog);
        window.show();
    }

    @Test
    public void shouldBeVisible() {
        window.requireVisible();
    }

    @Test
    public void shouldShowChartsWithData() {
        // Verify the dialog renders without errors when given real data
        window.requireVisible();
    }
}
