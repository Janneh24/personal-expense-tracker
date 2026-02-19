package com.example.expense.view;

import com.example.expense.model.Category;
import com.example.expense.model.Role;
import com.example.expense.model.User;
import com.example.expense.service.CategoryService;
import com.example.expense.service.ExpenseService;
import com.example.expense.service.UserService;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import static org.mockito.Mockito.*;

public class ExpenseJourneyTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private ExpenseService expenseService;
    private CategoryService categoryService;
    private UserService userService;
    private User currentUser;

    @Override
    protected void onSetUp() {
        expenseService = mock(ExpenseService.class);
        categoryService = mock(CategoryService.class);
        userService = mock(UserService.class);
        currentUser = new User(1L, "testuser", "password", Role.USER);

        // Mock initial data
        when(expenseService.getExpensesByUser(currentUser)).thenReturn(new ArrayList<>());
        when(categoryService.getAllCategories()).thenReturn(List.of(new Category("Food"), new Category("Transport")));

        MainView frame = GuiActionRunner
                .execute(() -> new MainView(userService, expenseService, categoryService, currentUser));
        window = new FrameFixture(robot(), frame);
        window.show();
    }

    @Test
    public void shouldOpenAddExpenseDialog() {
        System.out.println("Starting shouldOpenAddExpenseDialog...");
        // Click Add Expense button
        window.button("addExpenseButton").click();
        System.out.println("Clicked addExpenseButton");

        // Verify dialog opens
        DialogFixture dialog = window.dialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                boolean match = "Add Expense".equals(dialog.getTitle()) && dialog.isVisible();
                if (match)
                    System.out.println("Found matching dialog");
                return match;
            }
        });
        System.out.println("Dialog fixture obtained");

        // Verify form fields exist
        dialog.textBox("amount").requireVisible().requireEnabled();
        System.out.println("Amount field verified");
        dialog.textBox("description").requireVisible().requireEnabled();
        System.out.println("Description field verified");
        dialog.textBox("date").requireVisible().requireEnabled();
        System.out.println("Date field verified");
        dialog.comboBox("category").requireVisible().requireEnabled();
        System.out.println("Category field verified");
        dialog.button("saveButton").requireVisible().requireText("Save");
        System.out.println("Save button verified");

        // Close dialog
        dialog.close();
        System.out.println("Dialog closed");
    }
}
