package com.example.expense.view;

import com.example.expense.model.Expense;
import com.example.expense.model.User;
import com.example.expense.service.ExpenseService;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.FileOutputStream;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class ReportView extends JDialog {

    private final ExpenseService expenseService;
    private final User currentUser;
    private final ExpenseTableModel tableModel;
    private JLabel totalLabel;

    public ReportView(Frame owner, ExpenseService expenseService, User currentUser) {
        super(owner, "Generate Report", true);
        this.expenseService = expenseService;
        this.currentUser = currentUser;
        this.tableModel = new ExpenseTableModel();

        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(owner);

        // Control Panel
        JPanel controlPanel = new JPanel();
        String[] reportTypes = { "Daily", "Weekly", "Monthly", "Yearly" };
        JComboBox<String> typeCombo = new JComboBox<>(reportTypes);
        typeCombo.setName("typeCombo");
        JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
        dateField.setName("dateField");
        JButton generateButton = new JButton("Generate");
        generateButton.setName("generateButton");

        controlPanel.add(new JLabel("Type:"));
        controlPanel.add(typeCombo);
        controlPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        controlPanel.add(dateField);
        controlPanel.add(generateButton);

        JButton exportPdfButton = new JButton("Export to PDF");
        exportPdfButton.setName("exportPdfButton");
        exportPdfButton.addActionListener(e -> exportToPdf());
        controlPanel.add(exportPdfButton);

        JButton exportCsvButton = new JButton("Export to CSV");
        exportCsvButton.setName("exportCsvButton");
        exportCsvButton.addActionListener(e -> exportToCsv());
        controlPanel.add(exportCsvButton);

        add(controlPanel, BorderLayout.NORTH);

        // Table
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Footer Total
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total: 0.00");
        footerPanel.add(totalLabel);
        add(footerPanel, BorderLayout.SOUTH);

        generateButton
                .addActionListener(e -> generateReport((String) typeCombo.getSelectedItem(), dateField.getText()));
    }

    private void generateReport(String type, String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            List<Expense> expenses;

            switch (type) {
                case "Daily":
                    expenses = expenseService.getDailyReport(currentUser, date);
                    break;
                case "Weekly":
                    expenses = expenseService.getWeeklyReport(currentUser, date);
                    break;
                case "Monthly":
                    expenses = expenseService.getMonthlyReport(currentUser, date);
                    break;
                case "Yearly":
                    expenses = expenseService.getYearlyReport(currentUser, date);
                    break;
                default:
                    expenses = java.util.Collections.emptyList();
            }

            tableModel.setExpenses(expenses);
            updateTotal(expenses);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format (yyyy-MM-dd) or error: " + e.getMessage());
        }
    }

    private void updateTotal(List<Expense> expenses) {
        java.math.BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        totalLabel.setText("Total: " + total.toString());
    }

    private void exportToPdf() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export. Generate a report first.");
            return;
        }

        JFileChooser fileChooser = createFileChooser("Expense_Report.pdf");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try {
                expenseService.exportToPdf(tableModel.getExpenses(), file, currentUser.getUsername());
                JOptionPane.showMessageDialog(this, "PDF Exported successfully to " + file.getAbsolutePath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToCsv() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export. Generate a report first.");
            return;
        }

        JFileChooser fileChooser = createFileChooser("Expense_Report.csv");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try {
                expenseService.exportToCsv(tableModel.getExpenses(), file);
                JOptionPane.showMessageDialog(this, "CSV Exported successfully to " + file.getAbsolutePath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected JFileChooser createFileChooser(String defaultName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(defaultName));
        return fileChooser;
    }
}
