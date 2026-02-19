package com.example.expense.view;

import com.example.expense.model.Role;
import com.example.expense.model.User;
import com.example.expense.service.CategoryService;
import com.example.expense.service.ExpenseService;
import com.example.expense.service.UserService;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class LoginViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private UserService userService;
    private ExpenseService expenseService;
    private CategoryService categoryService;

    @Override
    protected void onSetUp() {
        userService = mock(UserService.class);
        expenseService = mock(ExpenseService.class);
        categoryService = mock(CategoryService.class);

        LoginView frame = GuiActionRunner.execute(() -> new LoginView(userService, expenseService, categoryService));
        window = new FrameFixture(robot(), frame);
        robot().settings().delayBetweenEvents(60);
        window.show();
    }

    @Test
    public void shouldDisplayLoginForm() {
        window.textBox("username").requireVisible().requireEnabled();
        window.textBox("password").requireVisible().requireEnabled();
        window.button("loginButton").requireVisible().requireEnabled().requireText("Login");
    }

    @Test
    public void shouldShowErrorOnInvalidCredentials() {
        when(userService.authenticate("wrong", "pass")).thenReturn(java.util.Optional.empty());

        window.textBox("username").enterText("wrong");
        window.textBox("password").enterText("pass");
        window.button("loginButton").click();
        window.robot().waitForIdle();

        window.optionPane().requireErrorMessage().requireMessage("Invalid credentials");
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldShowErrorOnRoleMismatch() {
        User user = new User(1L, "admin", "pass", Role.ADMIN);
        when(userService.authenticate("admin", "pass")).thenReturn(java.util.Optional.of(user));

        window.textBox("username").enterText("admin");
        window.textBox("password").enterText("pass");
        window.comboBox().selectItem("USER");
        window.button("loginButton").click();
        window.robot().waitForIdle();

        window.optionPane().requireErrorMessage().requireMessage("Invalid Role for this user.");
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldShowErrorOnDisabledAccount() {
        User user = new User(1L, "test", "pass", Role.USER);
        user.setEnabled(false);
        when(userService.authenticate("test", "pass")).thenReturn(java.util.Optional.of(user));

        window.textBox("username").enterText("test");
        window.textBox("password").enterText("pass");
        window.comboBox().selectItem("USER");
        window.button("loginButton").click();
        window.robot().waitForIdle();

        window.optionPane().requireErrorMessage()
                .requireMessage("Your account has been disabled. Please contact admin.");
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldLoginAsUserSuccessfully() {
        User user = new User(1L, "test", "pass", Role.USER);
        user.setEnabled(true);
        when(userService.authenticate("test", "pass")).thenReturn(java.util.Optional.of(user));
        when(expenseService.getExpensesByUser(user)).thenReturn(java.util.Collections.emptyList());

        window.textBox("username").enterText("test");
        window.textBox("password").enterText("pass");
        window.comboBox().selectItem("USER");
        window.button("loginButton").click();
        window.robot().waitForIdle();

        // LoginView should now be disposed
        try {
            window.requireNotVisible();
        } catch (org.assertj.swing.exception.ComponentLookupException e) {
            // Already gone - also acceptable
        }
    }

    @Test
    public void shouldLoginAsAdminSuccessfully() {
        User admin = new User(1L, "admin", "pass", Role.ADMIN);
        admin.setEnabled(true);
        when(userService.authenticate("admin", "pass")).thenReturn(java.util.Optional.of(admin));
        when(userService.getAllUsers()).thenReturn(java.util.Collections.emptyList());

        window.textBox("username").enterText("admin");
        window.textBox("password").enterText("pass");
        window.comboBox().selectItem("ADMIN");
        window.button("loginButton").click();
        window.robot().waitForIdle();

        // LoginView should now be disposed
        try {
            window.requireNotVisible();
        } catch (org.assertj.swing.exception.ComponentLookupException e) {
            // Already gone
        }
    }

    @Test
    public void shouldOpenRegisterDialog() {
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Create Account")).click();
        org.assertj.swing.fixture.DialogFixture regDialog = window
                .dialog(org.assertj.swing.core.matcher.DialogMatcher.withTitle("Register"));
        regDialog.requireVisible();
        regDialog.close();
    }
}
