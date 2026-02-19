package com.example.expense.service;

import com.example.expense.model.Expense;
import com.example.expense.model.User;
import com.example.expense.repository.ExpenseRepository;
import java.util.List;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @jakarta.inject.Inject
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense addExpense(Expense expense) {
        if (expense.getAmount() == null || expense.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Expense amount must be positive");
        }
        return expenseRepository.save(expense);
    }

    public List<Expense> getExpensesByUser(User user) {
        return expenseRepository.findByUser(user);
    }

    public List<Expense> getDailyReport(User user, java.time.LocalDate date) {
        return expenseRepository.findByUserAndDateBetween(user, date, date);
    }

    public List<Expense> getWeeklyReport(User user, java.time.LocalDate date) {
        java.time.LocalDate startOfWeek = date
                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        java.time.LocalDate endOfWeek = date
                .with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        return expenseRepository.findByUserAndDateBetween(user, startOfWeek, endOfWeek);
    }

    public List<Expense> getMonthlyReport(User user, java.time.LocalDate date) {
        java.time.LocalDate startOfMonth = date.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
        java.time.LocalDate endOfMonth = date.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
        return expenseRepository.findByUserAndDateBetween(user, startOfMonth, endOfMonth);
    }

    public List<Expense> getYearlyReport(User user, java.time.LocalDate date) {
        java.time.LocalDate startOfYear = date.with(java.time.temporal.TemporalAdjusters.firstDayOfYear());
        java.time.LocalDate endOfYear = date.with(java.time.temporal.TemporalAdjusters.lastDayOfYear());
        return expenseRepository.findByUserAndDateBetween(user, startOfYear, endOfYear);
    }

    public Expense updateExpense(Expense expense) {
        if (expense.getAmount() == null || expense.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Expense amount must be positive");
        }
        return expenseRepository.update(expense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.findById(id).ifPresent(expenseRepository::delete);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public void exportToCsv(List<Expense> expenses, java.io.File file) throws java.io.IOException {
        try (java.io.FileWriter writer = new java.io.FileWriter(file);
                com.opencsv.CSVWriter csvWriter = new com.opencsv.CSVWriter(writer)) {

            String[] header = { "Date", "Category", "Description", "Amount" };
            csvWriter.writeNext(header);

            for (Expense e : expenses) {
                String[] data = {
                        e.getDate().toString(),
                        e.getCategory() != null ? e.getCategory().getName() : "N/A",
                        e.getDescription(),
                        e.getAmount().toString()
                };
                csvWriter.writeNext(data);
            }
        }
    }

    public void exportToPdf(List<Expense> expenses, java.io.File file, String username) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Expense Report - " + username, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Meta info
        document.add(new Paragraph("Generated on: " + LocalDate.now()));
        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        document.add(new Paragraph("Total Spending: " + total.toString()));
        document.add(new Paragraph(" "));

        // Table
        PdfPTable pdfTable = new PdfPTable(4);
        pdfTable.setWidthPercentage(100);

        // Headers
        String[] headers = { "Date", "Category", "Description", "Amount" };
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
            pdfTable.addCell(cell);
        }

        // Data
        for (Expense e : expenses) {
            pdfTable.addCell(e.getDate().toString());
            pdfTable.addCell(e.getCategory() != null ? e.getCategory().getName() : "N/A");
            pdfTable.addCell(e.getDescription());
            pdfTable.addCell(e.getAmount().toString());
        }

        document.add(pdfTable);
        document.close();
    }
}
