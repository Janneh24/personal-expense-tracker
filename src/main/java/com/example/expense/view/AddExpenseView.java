package com.example.expense.view;

import com.example.expense.model.Category;
import com.example.expense.model.Expense;
import com.example.expense.model.User;
import com.example.expense.service.ExpenseService;
import com.example.expense.service.CategoryService;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddExpenseView extends JDialog {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final Runnable onSaveCallback;
    private final User currentUser;

    private JTextField amountField;
    private JTextField dateField;
    private JTextField descriptionField;
    private JComboBox<String> categoryComboBox;

    public AddExpenseView(java.awt.Frame parent, ExpenseService expenseService, CategoryService categoryService,
            User currentUser, Runnable onSaveCallback) {
        super(parent, "Add Expense", true);
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.currentUser = currentUser;
        this.onSaveCallback = onSaveCallback;

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        formPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        amountField.setName("amount");
        formPanel.add(amountField);

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(LocalDate.now().toString());
        dateField.setName("date");
        formPanel.add(dateField);

        formPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        descriptionField.setName("description");
        formPanel.add(descriptionField);

        formPanel.add(new JLabel("Category:"));
        List<Category> categories = categoryService.getAllCategories();
        String[] catNames = categories.stream().map(Category::getName).toArray(String[]::new);
        categoryComboBox = new JComboBox<>(catNames);
        categoryComboBox.setName("category");
        categoryComboBox.setEditable(true);
        formPanel.add(categoryComboBox);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        saveButton.setName("saveButton");
        saveButton.addActionListener(e -> saveExpense());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void saveExpense() {
        try {
            BigDecimal amount = new BigDecimal(amountField.getText());
            LocalDate date = LocalDate.parse(dateField.getText());
            String description = descriptionField.getText();
            String categoryName = (String) categoryComboBox.getSelectedItem();

            Category category = categoryService.getAllCategories().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                    .findFirst()
                    .orElseGet(() -> categoryService.saveCategory(categoryName));

            Expense expense = new Expense(amount, date, description, currentUser, category);
            expenseService.addExpense(expense);

            onSaveCallback.run();
            dispose();

        } catch (NumberFormatException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving expense: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
