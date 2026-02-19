package com.example.expense.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.mockito.Mockito.*;

import com.example.expense.model.Role;
import com.example.expense.service.UserService;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

public class RegistrationDialogTest extends AssertJSwingJUnitTestCase {

    private DialogFixture window;
    private UserService userService;
    private RegistrationDialog dialog;

    @Override
    protected void onSetUp() {
        userService = mock(UserService.class);

        dialog = execute(() -> new RegistrationDialog(null, userService));
        window = new DialogFixture(robot(), dialog);
        robot().settings().delayBetweenEvents(200);
        window.show();
        window.robot().waitForIdle();
        org.assertj.swing.timing.Pause.pause(500);
    }

    @Test
    public void shouldDisplayRegistrationForm() {
        window.textBox("regUsername").requireVisible().requireEnabled();
        window.textBox("regPassword").requireVisible().requireEnabled();
        window.button("regOkButton").requireVisible().requireEnabled().requireText("OK");
        window.button("regCancelButton").requireVisible().requireEnabled().requireText("Cancel");
    }

    @Test
    public void shouldShowErrorOnEmptyFields() {
        window.button("regOkButton").click();

        window.optionPane().requireMessage("Username and password cannot be empty");
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldRegisterSuccessfully() {
        when(userService.registerUser(anyString(), anyString(), eq(Role.USER)))
                .thenReturn(new com.example.expense.model.User(1L, "newuser", "hash", Role.USER));

        window.textBox("regUsername").enterText("newuser");
        window.textBox("regPassword").enterText("newpass");
        window.button("regOkButton").click();

        verify(userService).registerUser("newuser", "newpass", Role.USER);
        window.optionPane()
                .requireMessage("Registration successful! use your new credentials to login.");
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldShowErrorOnRegistrationFailure() {
        when(userService.registerUser(anyString(), anyString(), eq(Role.USER)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        window.textBox("regUsername").enterText("existing");
        window.textBox("regPassword").enterText("pass");
        window.button("regOkButton").click();
        window.robot().waitForIdle();

        window.optionPane().requireMessage("Registration failed: Username already exists");
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldReturnTrueAfterSuccessfulRegistration() {
        when(userService.registerUser(anyString(), anyString(), eq(Role.USER)))
                .thenReturn(new com.example.expense.model.User(1L, "u", "h", Role.USER));

        assertThat(dialog.isSuccessful()).isFalse();

        window.textBox("regUsername").enterText("u");
        window.textBox("regPassword").enterText("p");
        window.button("regOkButton").click();
        window.optionPane().okButton().click();

        assertThat(dialog.isSuccessful()).isTrue();
    }

    @Test
    public void shouldCancelWithoutRegistering() {
        window.button("regCancelButton").click();

        verify(userService, never()).registerUser(anyString(), anyString(), any());
        assertThat(dialog.isSuccessful()).isFalse();
    }
}
