package com.example.expense.view;

import static org.mockito.Mockito.*;
import static org.assertj.swing.edt.GuiActionRunner.execute;

import com.example.expense.model.Role;
import com.example.expense.model.User;
import com.example.expense.service.CategoryService;
import com.example.expense.service.ExpenseService;
import com.example.expense.service.UserService;
import java.util.ArrayList;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class AdminDashboardViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private UserService userService;
    private ExpenseService expenseService;
    private CategoryService categoryService;
    private User admin;

    @Override
    protected void onSetUp() {
        userService = mock(UserService.class);
        expenseService = mock(ExpenseService.class);
        categoryService = mock(CategoryService.class);
        admin = new User(1L, "admin", "admin", Role.ADMIN);

        when(userService.getAllUsers()).thenReturn(new ArrayList<>());
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        AdminDashboardView frame = execute(
                () -> new AdminDashboardView(userService, expenseService, categoryService, admin));
        window = new FrameFixture(robot(), frame);
        robot().settings().delayBetweenEvents(300);
        window.show();
        robot().waitForIdle();
        org.assertj.swing.timing.Pause.pause(500);
    }

    @Test
    public void shouldAddUser() {
        window.textBox("addUserUsername").enterText("newuser");
        window.textBox("addUserPassword").enterText("newpass");
        window.comboBox("addUserRole").selectItem("USER");
        window.button("addUserButton").click();
        org.assertj.swing.timing.Pause.pause(500);
        window.robot().waitForIdle();

        verify(userService).registerUser("newuser", "newpass", Role.USER);
    }

    @Test
    public void shouldToggleUserStatus() {
        User target = new User(2L, "target", "pass", Role.USER);
        target.setEnabled(true);
        java.util.List<User> users = new ArrayList<>();
        users.add(target);
        when(userService.getAllUsers()).thenReturn(users);
        window.tabbedPane().selectTab(0);

        // Refresh to show user
        window.button("refreshUsersButton").click();
        org.assertj.swing.timing.Pause.pause(new org.assertj.swing.timing.Condition("table has rows") {
            @Override
            public boolean test() {
                return execute(() -> window.table().target().getRowCount() > 0);
            }
        }, 10000);
        window.robot().waitForIdle();

        window.table().selectRows(0);
        window.button("toggleStatusButton").click();
        org.assertj.swing.timing.Pause.pause(500);
        window.robot().waitForIdle();

        verify(userService).setUserStatus(2L, false); // Toggle from true to false
    }

    @Test
    public void shouldResetPassword() {
        User target = new User(2L, "target", "pass", Role.USER);
        java.util.List<User> users = new ArrayList<>();
        users.add(target);
        when(userService.getAllUsers()).thenReturn(users);
        window.tabbedPane().selectTab(0);

        window.button("refreshUsersButton").click();
        org.assertj.swing.timing.Pause.pause(new org.assertj.swing.timing.Condition("table has rows") {
            @Override
            public boolean test() {
                return execute(() -> window.table().target().getRowCount() > 0);
            }
        }, 10000);
        window.robot().waitForIdle();
        window.table().selectRows(0);

        // Mock the input dialog
        // This is tricky with JOptionPane.showInputDialog.
        // We can use a custom robot or just verify the button click and dialog
        // appearance.
        window.button("resetPassButton").click();
        org.assertj.swing.timing.Pause.pause(500);
        window.robot().waitForIdle();
        window.optionPane().requireMessage("Enter new password:");
        window.optionPane().textBox().enterText("secret");
        window.optionPane().okButton().click();
        org.assertj.swing.timing.Pause.pause(500);
        window.robot().waitForIdle();

        verify(userService).resetPassword(2L, "secret");
        window.optionPane().requireMessage("Password reset successfully");
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldLogout() {
        window.menuItem("logoutItem").click();
        assertThat(window.target().isVisible()).isFalse();
    }

    @Test
    public void shouldOpenCategoryManagement() {
        window.robot().waitForIdle();
        window.menuItem("categoryItem").click();
        org.assertj.swing.fixture.DialogFixture diag = window.dialog(
                org.assertj.swing.core.matcher.DialogMatcher.withTitle("Category Management"),
                org.assertj.swing.timing.Timeout.timeout(5000));
        diag.requireVisible();
        diag.close();
    }

    @Test
    public void shouldOpenPlatformInsights() {
        window.robot().waitForIdle();
        window.menuItem("insightItem").click();
        org.assertj.swing.fixture.DialogFixture diag = window.dialog(
                org.assertj.swing.core.matcher.DialogMatcher.withTitle("Platform Global Insights"),
                org.assertj.swing.timing.Timeout.timeout(5000));
        diag.requireVisible();
        diag.close();
    }

    @Test
    public void shouldRefreshExpenses() {
        window.tabbedPane().selectTab(1);
        org.assertj.swing.timing.Pause.pause(new org.assertj.swing.timing.Condition("button is showing") {
            @Override
            public boolean test() {
                return window.button("refreshExpensesButton").target().isShowing();
            }
        }, 5000);
        window.button("refreshExpensesButton").click();
        verify(expenseService, atLeastOnce()).getAllExpenses();
    }

    @Test
    public void shouldShowErrorOnAddUserFailure() {
        doThrow(new RuntimeException("Existing user")).when(userService).registerUser(anyString(), anyString(), any());

        window.textBox("addUserUsername").enterText("test");
        window.textBox("addUserPassword").enterText("pass");
        window.button("addUserButton").click();

        org.assertj.swing.timing.Pause.pause(1000);

        window.optionPane(org.assertj.swing.timing.Timeout.timeout(5000))
                .requireMessage("Error: Existing user");
        window.optionPane().okButton().click();
    }
}
