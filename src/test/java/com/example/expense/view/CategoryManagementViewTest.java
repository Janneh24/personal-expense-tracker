package com.example.expense.view;

import static org.mockito.Mockito.*;
import static org.assertj.swing.edt.GuiActionRunner.execute;

import com.example.expense.model.Category;
import com.example.expense.service.CategoryService;
import java.util.ArrayList;
import java.util.List;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

public class CategoryManagementViewTest extends AssertJSwingJUnitTestCase {

    private DialogFixture window;
    private CategoryService categoryService;

    @Override
    protected void onSetUp() {
        categoryService = mock(CategoryService.class);

        Category c1 = new Category("Food");
        c1.setId(1L);
        c1.setSystem(true);
        Category c2 = new Category("Entertainment");
        c2.setId(2L);
        c2.setSystem(false);

        List<Category> categories = new ArrayList<>();
        categories.add(c1);
        categories.add(c2);
        when(categoryService.getAllCategories()).thenReturn(categories);

        CategoryManagementView dialog = execute(() -> new CategoryManagementView(null, categoryService));
        window = new DialogFixture(robot(), dialog);
        robot().settings().delayBetweenEvents(100);
        window.show();
        robot().waitForIdle();
    }

    @Test
    public void shouldDisplayCategoriesInTable() {
        window.table("categoryTable").requireRowCount(2);
    }

    @Test
    public void shouldAddCategory() {
        window.textBox("categoryNameField").enterText("Travel");
        window.button("addCategoryButton").click();
        window.robot().waitForIdle();

        verify(categoryService).saveCategory("Travel");
    }

    @Test
    public void shouldNotAddEmptyCategory() {
        // Leave text field empty and click add
        window.button("addCategoryButton").click();
        window.robot().waitForIdle();

        verify(categoryService, never()).saveCategory(anyString());
    }

    @Test
    public void shouldShowErrorOnAddCategoryFailure() {
        doThrow(new RuntimeException("Duplicate category")).when(categoryService).saveCategory("Food");

        window.textBox("categoryNameField").enterText("Food");
        window.button("addCategoryButton").click();
        window.robot().waitForIdle();

        window.optionPane().requireMessage("Error: Duplicate category");
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldDeleteCustomCategory() {
        // Select row 1 (Entertainment - Custom)
        window.table("categoryTable").selectRows(1);
        window.button("deleteCategoryButton").click();

        verify(categoryService).deleteCategory(2L);
    }

    @Test
    public void shouldNotDeleteSystemCategory() {
        // Select row 0 (Food - System)
        window.table("categoryTable").selectRows(0);
        window.button("deleteCategoryButton").click();
        window.robot().waitForIdle();

        window.optionPane().requireMessage("System categories cannot be deleted.");
        window.optionPane().okButton().click();

        verify(categoryService, never()).deleteCategory(anyLong());
    }

    @Test
    public void shouldNotDeleteWhenNoRowSelected() {
        // Click delete without selecting any row
        window.button("deleteCategoryButton").click();

        verify(categoryService, never()).deleteCategory(anyLong());
    }
}
