package com.example.expense.view;

import static org.mockito.Mockito.*;
import static org.assertj.swing.edt.GuiActionRunner.execute;

import com.example.expense.model.Category;
import com.example.expense.model.Role;
import com.example.expense.model.User;
import com.example.expense.service.CategoryService;
import com.example.expense.service.ExpenseService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

public class AddExpenseViewTest extends AssertJSwingJUnitTestCase {

    private DialogFixture window;
    private ExpenseService expenseService;
    private CategoryService categoryService;
    private Runnable onSaveCallback;
    private User currentUser;

    @Override
    protected void onSetUp() {
        expenseService = mock(ExpenseService.class);
        categoryService = mock(CategoryService.class);
        onSaveCallback = mock(Runnable.class);
        currentUser = new User(1L, "test", "pass", Role.USER);

        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Food"));
        when(categoryService.getAllCategories()).thenReturn(categories);

        AddExpenseView dialog = execute(
                () -> new AddExpenseView(null, expenseService, categoryService, currentUser, onSaveCallback));
        window = new DialogFixture(robot(), dialog);
        robot().settings().delayBetweenEvents(200);
        // Modal dialogs block setVisible(true), so run in background
        javax.swing.SwingUtilities.invokeLater(() -> dialog.setVisible(true));
        org.assertj.swing.timing.Pause.pause(500);
    }

    @Test
    public void shouldInitializeWithDefaults() {
        window.textBox("date").requireText(LocalDate.now().toString());
        window.comboBox("category").requireItemCount(1).requireSelection("Food");
    }

    @Test
    public void shouldShowErrorOnInvalidInput() {
        window.textBox("amount").enterText("not-a-number");
        window.button("saveButton").click();
        org.assertj.swing.timing.Pause.pause(500);
        window.robot().waitForIdle();

        window.optionPane().requireMessage(java.util.regex.Pattern.compile(".*Invalid input.*"));
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldSaveExpenseAndCallCallback() {
        window.textBox("amount").enterText("100.50");
        window.textBox("description").enterText("Lunch");
        window.button("saveButton").click();
        org.assertj.swing.timing.Pause.pause(1000);
        window.robot().waitForIdle();

        verify(expenseService).addExpense(any());
        verify(onSaveCallback).run();
    }

    @Test
    public void shouldShowErrorOnInvalidDate() {
        window.textBox("date").deleteText().enterText("invalid-date");
        window.button("saveButton").click();
        window.robot().waitForIdle();

        window.optionPane().requireMessage(java.util.regex.Pattern.compile(".*Invalid input.*"));
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldCreateNewCategoryIfNotFound() {
        window.textBox("amount").enterText("50.00");
        window.textBox("description").enterText("New Cat Test");
        window.comboBox("category").click();
        window.comboBox("category").selectAllText();
        window.comboBox("category").enterText("Travel");
        window.robot().pressAndReleaseKey(java.awt.event.KeyEvent.VK_ENTER);
        window.robot().waitForIdle();

        when(categoryService.saveCategory("Travel")).thenReturn(new Category("Travel"));

        window.button("saveButton").click();
        org.assertj.swing.timing.Pause.pause(new org.assertj.swing.timing.Condition("category saved") {
            @Override
            public boolean test() {
                try {
                    verify(categoryService).saveCategory("Travel");
                    return true;
                } catch (Throwable t) {
                    return false;
                }
            }
        }, 5000);

        verify(expenseService).addExpense(any());
        verify(onSaveCallback).run();
    }

    @Test
    public void shouldShowErrorOnSaveException() {
        window.textBox("amount").enterText("50.00");
        window.textBox("description").enterText("Error Test");

        doThrow(new RuntimeException("Database error")).when(expenseService).addExpense(any());

        window.button("saveButton").click();
        org.assertj.swing.timing.Pause.pause(1000);
        window.robot().waitForIdle();

        window.optionPane().requireMessage("Error saving expense: Database error");
        window.optionPane().okButton().click();
    }
}
