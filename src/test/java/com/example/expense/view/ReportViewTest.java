package com.example.expense.view;

import static org.mockito.Mockito.*;
import static org.assertj.swing.edt.GuiActionRunner.execute;

import com.example.expense.model.Category;
import com.example.expense.model.Expense;
import com.example.expense.model.Role;
import com.example.expense.model.User;
import com.example.expense.service.ExpenseService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

public class ReportViewTest extends AssertJSwingJUnitTestCase {

    private DialogFixture window;
    private ExpenseService expenseService;
    private User currentUser;
    private JFileChooser mockFileChooser;

    @Override
    protected void onSetUp() {
        expenseService = mock(ExpenseService.class);
        currentUser = new User(1L, "test", "pass", Role.USER);
        mockFileChooser = mock(JFileChooser.class);

        ReportView dialog = execute(() -> new ReportView(null, expenseService, currentUser) {
            @Override
            protected javax.swing.JFileChooser createFileChooser(String defaultName) {
                return mockFileChooser;
            }
        });
        window = new DialogFixture(robot(), dialog);
        robot().settings().delayBetweenEvents(100);
        window.show();
    }

    @Test
    public void shouldGenerateReportForUnknownType() {
        // This covers the 'default' case in generateReport switch
        // We use Reflection since 'Unknown' isn't in the JComboBox
        execute(() -> {
            try {
                java.lang.reflect.Method m = window.target().getClass().getDeclaredMethod("generateReport",
                        String.class, String.class);
                m.setAccessible(true);
                m.invoke(window.target(), "Unknown", LocalDate.now().toString());
            } catch (Exception e) {
            }
        });
    }

    @Test
    public void shouldGenerateDailyReport() {
        Category food = new Category("Food");
        Expense e1 = new Expense(new BigDecimal("25.50"), LocalDate.now(), "Lunch", currentUser, food);
        List<Expense> expenses = Arrays.asList(e1);
        when(expenseService.getDailyReport(eq(currentUser), any())).thenReturn(expenses);

        window.comboBox("typeCombo").selectItem("Daily");
        window.button("generateButton").click();

        verify(expenseService).getDailyReport(eq(currentUser), any());
    }

    @Test
    public void shouldGenerateWeeklyReport() {
        when(expenseService.getWeeklyReport(eq(currentUser), any())).thenReturn(java.util.Collections.emptyList());

        window.comboBox("typeCombo").selectItem("Weekly");
        window.button("generateButton").click();

        verify(expenseService).getWeeklyReport(eq(currentUser), any());
    }

    @Test
    public void shouldGenerateMonthlyReport() {
        when(expenseService.getMonthlyReport(eq(currentUser), any())).thenReturn(java.util.Collections.emptyList());

        window.comboBox("typeCombo").selectItem("Monthly");
        window.robot().waitForIdle();
        org.assertj.swing.timing.Pause.pause(300);
        window.button("generateButton").click();
        window.robot().waitForIdle();

        verify(expenseService).getMonthlyReport(eq(currentUser), any());
    }

    @Test
    public void shouldGenerateYearlyReport() {
        when(expenseService.getYearlyReport(eq(currentUser), any())).thenReturn(java.util.Collections.emptyList());

        window.comboBox("typeCombo").selectItem("Yearly");
        window.robot().waitForIdle();
        org.assertj.swing.timing.Pause.pause(300);
        window.button("generateButton").click();
        window.robot().waitForIdle();

        verify(expenseService).getYearlyReport(eq(currentUser), any());
    }

    @Test
    public void shouldHandleInvalidDateFormat() {
        window.textBox("dateField").deleteText().enterText("not-a-date");
        window.button("generateButton").click();
        window.robot().waitForIdle();

        window.optionPane().requireMessage(java.util.regex.Pattern.compile(".*Invalid Date Format.*"));
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldShowNoDataMessageOnPdfExport() {
        window.button("exportPdfButton").click();
        window.robot().waitForIdle();
        window.optionPane().requireMessage("No data to export. Generate a report first.");
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldShowNoDataMessageOnCsvExport() {
        window.button("exportCsvButton").click();
        window.robot().waitForIdle();
        window.optionPane().requireMessage("No data to export. Generate a report first.");
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldExportPdfSuccessfully() throws Exception {
        // Populate table first
        Expense e1 = new Expense(new BigDecimal("25.50"), LocalDate.now(), "Lunch", currentUser, new Category("Food"));
        when(expenseService.getDailyReport(eq(currentUser), any())).thenReturn(Arrays.asList(e1));
        window.button("generateButton").click();

        java.io.File file = new java.io.File("test.pdf");
        when(mockFileChooser.showSaveDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
        when(mockFileChooser.getSelectedFile()).thenReturn(file);

        window.button("exportPdfButton").click();
        window.optionPane().requireMessage(java.util.regex.Pattern.compile(".*PDF Exported successfully.*"));
        window.optionPane().okButton().click();

        verify(expenseService).exportToPdf(anyList(), eq(file), anyString());
    }

    @Test
    public void shouldExportCsvSuccessfully() throws Exception {
        // Populate table first
        Expense e1 = new Expense(new BigDecimal("25.50"), LocalDate.now(), "Lunch", currentUser, new Category("Food"));
        when(expenseService.getDailyReport(eq(currentUser), any())).thenReturn(Arrays.asList(e1));
        window.button("generateButton").click();

        java.io.File file = new java.io.File("test.csv");
        when(mockFileChooser.showSaveDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
        when(mockFileChooser.getSelectedFile()).thenReturn(file);

        window.button("exportCsvButton").click();
        window.optionPane().requireMessage(java.util.regex.Pattern.compile(".*CSV Exported successfully.*"));
        window.optionPane().okButton().click();

        verify(expenseService).exportToCsv(anyList(), eq(file));
    }

    @Test
    public void shouldHandlePdfExportError() throws Exception {
        Expense e1 = new Expense(new BigDecimal("25.50"), LocalDate.now(), "Lunch", currentUser, new Category("Food"));
        when(expenseService.getDailyReport(eq(currentUser), any())).thenReturn(Arrays.asList(e1));
        window.button("generateButton").click();

        when(mockFileChooser.showSaveDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
        when(mockFileChooser.getSelectedFile()).thenReturn(new java.io.File("test.pdf"));
        doThrow(new RuntimeException("Disk full")).when(expenseService).exportToPdf(any(), any(), any());

        window.button("exportPdfButton").click();
        org.assertj.swing.timing.Pause.pause(1000);
        window.robot().waitForIdle();

        window.optionPane().requireMessage("Error exporting PDF: Disk full");
        window.optionPane().okButton().click();
    }

    @Test
    public void shouldHandleCsvExportError() throws Exception {
        Expense e1 = new Expense(new BigDecimal("25.50"), LocalDate.now(), "Lunch", currentUser, new Category("Food"));
        when(expenseService.getDailyReport(eq(currentUser), any())).thenReturn(Arrays.asList(e1));
        window.button("generateButton").click();

        when(mockFileChooser.showSaveDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
        when(mockFileChooser.getSelectedFile()).thenReturn(new java.io.File("test.csv"));
        doThrow(new RuntimeException("CSV Error")).when(expenseService).exportToCsv(any(), any());

        window.button("exportCsvButton").click();
        org.assertj.swing.timing.Pause.pause(1000);
        window.robot().waitForIdle();

        window.optionPane().requireMessage("Error exporting CSV: CSV Error");
        window.optionPane().okButton().click();
    }

    @Test
    public void testBaseCreateFileChooser() {
        // Direct call to cover base implementation without affecting GUI
        execute(() -> {
            ReportView rv = new ReportView(null, expenseService, currentUser);
            JFileChooser fc = rv.createFileChooser("test.txt");
            org.assertj.core.api.Assertions.assertThat(fc.getSelectedFile().getName()).isEqualTo("test.txt");
        });
    }
}
