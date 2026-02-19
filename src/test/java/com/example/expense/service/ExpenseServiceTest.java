package com.example.expense.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.expense.model.Category;
import com.example.expense.model.Expense;
import com.example.expense.model.User;
import com.example.expense.repository.ExpenseRepository;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @TempDir
    File tempDir;

    @Test
    void addExpense_shouldSaveExpense_whenValid() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100.00"));
        expense.setDate(LocalDate.now());

        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        Expense saved = expenseService.addExpense(expense);

        assertThat(saved).isNotNull();
        assertThat(saved.getAmount()).isEqualTo(expense.getAmount());
        assertThat(saved.getDate()).isEqualTo(expense.getDate());
        verify(expenseRepository).save(expense);
    }

    @Test
    void addExpense_shouldThrowException_whenAmountIsNegative() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("-10.00"));

        assertThatThrownBy(() -> expenseService.addExpense(expense))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expense amount must be positive");
    }

    @Test
    void addExpense_shouldThrowException_whenAmountIsZero() {
        Expense expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);

        assertThatThrownBy(() -> expenseService.addExpense(expense))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expense amount must be positive");
    }

    @Test
    void getExpensesByUser_shouldReturnList() {
        User user = new User();
        user.setId(1L);
        List<Expense> expected = List.of(new Expense());
        when(expenseRepository.findByUser(user)).thenReturn(expected);

        List<Expense> expenses = expenseService.getExpensesByUser(user);

        assertThat(expenses).isEqualTo(expected);
        verify(expenseRepository).findByUser(user);
    }

    @Test
    void getDailyReport_shouldReturnListFromRepository() {
        User user = new User();
        LocalDate date = LocalDate.of(2023, 1, 15);
        List<Expense> expected = List.of(new Expense());
        when(expenseRepository.findByUserAndDateBetween(user, date, date)).thenReturn(expected);

        List<Expense> result = expenseService.getDailyReport(user, date);

        assertThat(result).isEqualTo(expected);
        verify(expenseRepository).findByUserAndDateBetween(user, date, date);
    }

    @Test
    void getWeeklyReport_shouldReturnListFromRepository() {
        User user = new User();
        LocalDate date = LocalDate.of(2023, 1, 18); // Wednesday
        LocalDate start = LocalDate.of(2023, 1, 16); // Monday
        LocalDate end = LocalDate.of(2023, 1, 22); // Sunday
        List<Expense> expected = List.of(new Expense());
        when(expenseRepository.findByUserAndDateBetween(user, start, end)).thenReturn(expected);

        List<Expense> result = expenseService.getWeeklyReport(user, date);

        assertThat(result).isEqualTo(expected);
        verify(expenseRepository).findByUserAndDateBetween(user, start, end);
    }

    @Test
    void getMonthlyReport_shouldReturnListFromRepository() {
        User user = new User();
        LocalDate date = LocalDate.of(2023, 1, 15);
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);
        List<Expense> expected = List.of(new Expense());
        when(expenseRepository.findByUserAndDateBetween(user, start, end)).thenReturn(expected);

        List<Expense> result = expenseService.getMonthlyReport(user, date);

        assertThat(result).isEqualTo(expected);
        verify(expenseRepository).findByUserAndDateBetween(user, start, end);
    }

    @Test
    void getYearlyReport_shouldReturnListFromRepository() {
        User user = new User();
        LocalDate date = LocalDate.of(2023, 6, 15);
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        List<Expense> expected = List.of(new Expense());
        when(expenseRepository.findByUserAndDateBetween(user, start, end)).thenReturn(expected);

        List<Expense> result = expenseService.getYearlyReport(user, date);

        assertThat(result).isEqualTo(expected);
        verify(expenseRepository).findByUserAndDateBetween(user, start, end);
    }

    @Test
    void updateExpense_shouldUpdate_whenValid() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100.00"));
        when(expenseRepository.update(expense)).thenReturn(expense);

        Expense result = expenseService.updateExpense(expense);

        assertThat(result).isEqualTo(expense);
        verify(expenseRepository).update(expense);
    }

    @Test
    void updateExpense_shouldThrow_whenInvalid() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("-1.00"));

        assertThatThrownBy(() -> expenseService.updateExpense(expense))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateExpense_shouldThrow_whenAmountIsZero() {
        Expense expense = new Expense();
        expense.setAmount(BigDecimal.ZERO);

        assertThatThrownBy(() -> expenseService.updateExpense(expense))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expense amount must be positive");
    }

    @Test
    void deleteExpense_shouldDelete_whenExists() {
        Long id = 1L;
        Expense expense = new Expense();
        when(expenseRepository.findById(id)).thenReturn(Optional.of(expense));

        expenseService.deleteExpense(id);

        verify(expenseRepository).delete(expense);
    }

    @Test
    void deleteExpense_shouldNotDelete_whenNotExists() {
        Long id = 1L;
        when(expenseRepository.findById(id)).thenReturn(Optional.empty());

        expenseService.deleteExpense(id);

        verify(expenseRepository, never()).delete(any());
    }

    @Test
    void getAllExpenses_shouldReturnList() {
        when(expenseRepository.findAll()).thenReturn(List.of(new Expense()));
        assertThat(expenseService.getAllExpenses()).hasSize(1);
    }

    @Test
    void exportToCsv_shouldCreateFile() throws Exception {
        File file = new File(tempDir, "test.csv");
        List<Expense> expenses = Arrays.asList(
                new Expense(new BigDecimal("10.00"), LocalDate.now(), "Desc", new User(), new Category("Cat")));

        expenseService.exportToCsv(expenses, file);

        assertThat(file).exists();
        assertThat(file.length()).isGreaterThan(0);

        // Verify content
        java.nio.file.Path path = file.toPath();
        List<String> lines = java.nio.file.Files.readAllLines(path);
        assertThat(lines).hasSize(2);
        assertThat(lines.get(0)).contains("Date", "Category", "Description", "Amount");
        assertThat(lines.get(1)).contains("10.00", "Desc", "Cat");
    }

    @Test
    void exportToPdf_shouldCreateFile() throws Exception {
        File file = new File(tempDir, "test.pdf");
        LocalDate now = LocalDate.now();
        List<Expense> expenses = Arrays.asList(
                new Expense(new BigDecimal("10.00"), now, "Desc1", new User(), new Category("Cat1")));

        expenseService.exportToPdf(expenses, file, "testuser");

        assertThat(file).exists();
        assertThat(file.length()).isGreaterThan(0);

        // Verify content using OpenPDF's parser (consistent with BDD tests)
        try (com.lowagie.text.pdf.PdfReader reader = new com.lowagie.text.pdf.PdfReader(file.getAbsolutePath())) {
            com.lowagie.text.pdf.parser.PdfTextExtractor extractor = new com.lowagie.text.pdf.parser.PdfTextExtractor(
                    reader);
            StringBuilder content = new StringBuilder();
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                content.append(extractor.getTextFromPage(i));
            }
            String pdfText = content.toString();
            assertThat(pdfText).contains("Expense Report - testuser");
            assertThat(pdfText).contains("Total Spending: 10.00");
            assertThat(pdfText).contains("Date", "Category", "Description", "Amount");
            assertThat(pdfText).contains(now.toString());
            assertThat(pdfText).contains("Cat1");
            assertThat(pdfText).contains("Desc1");
            assertThat(pdfText).contains("10.00");
        }
    }

    @Test
    void addExpense_shouldThrowException_whenAmountIsNull() {
        Expense expense = new Expense();
        expense.setAmount(null);
        assertThatThrownBy(() -> expenseService.addExpense(expense))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expense amount must be positive");
    }

    @Test
    void updateExpense_shouldThrowException_whenAmountIsNull() {
        Expense expense = new Expense();
        expense.setAmount(null);
        assertThatThrownBy(() -> expenseService.updateExpense(expense))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expense amount must be positive");
    }
}
