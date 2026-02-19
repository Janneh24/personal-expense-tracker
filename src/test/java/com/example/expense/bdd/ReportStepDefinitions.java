package com.example.expense.bdd;

import com.example.expense.model.Category;
import com.example.expense.model.Expense;
import com.example.expense.model.Role;
import com.example.expense.model.User;
import com.example.expense.repository.ExpenseRepository;
import com.example.expense.service.ExpenseService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ReportStepDefinitions {

    private static ExpenseService expenseService = mock(ExpenseService.class);
    private static User currentUser = new User(1L, "testuser", "pass", Role.USER);
    private static List<Expense> mockExpenses = new ArrayList<>();
    private static File exportedFile;

    @Given("I have {int} expenses in my account")
    public void i_have_expenses_in_my_account(Integer count) {
        mockExpenses.clear();
        for (int i = 0; i < count; i++) {
            mockExpenses.add(new Expense(new BigDecimal("10.00"), LocalDate.now(), "Test " + i, currentUser,
                    new Category("Food")));
        }
        when(expenseService.getMonthlyReport(eq(currentUser), any(LocalDate.class))).thenReturn(mockExpenses);
    }

    @When("I generate a {string} report for today")
    public void i_generate_a_report_for_today(String type) {
        // In a real E2E we would use Robot, here we simulate the service call
        if (type.equals("Monthly")) {
            expenseService.getMonthlyReport(currentUser, LocalDate.now());
        }
    }

    @When("I click {string}")
    public void i_click(String buttonName) throws Exception {
        if (buttonName.equals("Export to PDF")) {
            exportedFile = new File("Expense_Report.pdf");
            // Use a real service or the logic to generate the PDF
            ExpenseRepository mockRepo = mock(ExpenseRepository.class);
            ExpenseService realService = new ExpenseService(mockRepo);
            realService.exportToPdf(mockExpenses, exportedFile, currentUser.getUsername());
        }
    }

    @Then("a PDF file {string} should be created")
    public void a_pdf_file_should_be_created(String fileName) {
        assertTrue("File should exist", exportedFile != null && exportedFile.exists());
        assertTrue("File name should match", exportedFile.getName().equals(fileName));
    }

    @Then("the PDF should contain {string}")
    public void the_pdf_should_contain(String text) throws Exception {
        try (com.lowagie.text.pdf.PdfReader reader = new com.lowagie.text.pdf.PdfReader(
                exportedFile.getAbsolutePath())) {
            com.lowagie.text.pdf.parser.PdfTextExtractor extractor = new com.lowagie.text.pdf.parser.PdfTextExtractor(
                    reader);
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                builder.append(extractor.getTextFromPage(i));
            }
            String content = builder.toString();
            assertTrue("Content should contain text '" + text + "'", content.contains(text));
        } finally {
            // Cleanup in finally to ensure it runs even if assertion fails
            if (exportedFile.exists()) {
                exportedFile.delete();
            }
        }
    }
}
