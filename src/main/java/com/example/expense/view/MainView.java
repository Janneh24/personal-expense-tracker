package com.example.expense.view;

import com.example.expense.model.Expense;
import com.example.expense.model.User;
import com.example.expense.model.Category;
import com.example.expense.service.ExpenseService;
import com.example.expense.service.UserService;
import com.example.expense.service.CategoryService;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.math.BigDecimal;

public class MainView extends JFrame {

    private final UserService userService;
    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final User currentUser;
    private final ExpenseTableModel tableModel;
    private TableRowSorter<ExpenseTableModel> sorter;

    private JLabel totalSpendingLabel;
    private JLabel topCategoryLabel;
    private JLabel budgetValueLabel;
    private JPanel budgetCard;
    private JLabel statusLabel;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public MainView(UserService userService, ExpenseService expenseService,
            CategoryService categoryService, User currentUser) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.currentUser = currentUser;
        this.tableModel = new ExpenseTableModel();

        initializeUI();
        createMenuBar();
        refreshExpenses();
    }

    private void initializeUI() {
        setTitle("Expense Tracker - " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        summaryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        totalSpendingLabel = new JLabel("€0.00");
        topCategoryLabel = new JLabel("N/A");
        budgetValueLabel = new JLabel("€" + df.format(currentUser.getMonthlyBudget()));

        summaryPanel.add(createSummaryCard("Total Spending", totalSpendingLabel, new Color(230, 242, 255)));
        budgetCard = createSummaryCard("Monthly Budget", budgetValueLabel, new Color(235, 255, 235));
        summaryPanel.add(budgetCard);
        summaryPanel.add(createSummaryCard("Top Category", topCategoryLabel, new Color(255, 245, 230)));

        northPanel.add(summaryPanel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Search: "));
        JTextField searchField = new JTextField(20);
        searchField.setName("searchField");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            public void changedUpdate(DocumentEvent e) {
                filter();
            }

            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        filterPanel.add(searchField);
        northPanel.add(filterPanel);

        add(northPanel, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Expense");
        addButton.setName("addExpenseButton");
        addButton.addActionListener(e -> showAddExpenseDialog());
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setName("mainDeleteButton");
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int response = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this expense?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    Expense expense = tableModel
                            .getExpenseAt(table.convertRowIndexToModel(selectedRow));
                    expenseService.deleteExpense(expense.getId());
                    refreshExpenses();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select an expense to delete");
            }
        });
        buttonPanel.add(deleteButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setName("mainRefreshButton");
        refreshButton.addActionListener(e -> refreshExpenses());
        buttonPanel.add(refreshButton);

        JButton reportButton = new JButton("Reports");
        reportButton.setName("mainReportButton");
        reportButton.addActionListener(e -> new ReportView(this, expenseService, currentUser).setVisible(true));
        buttonPanel.add(reportButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setName("mainLogoutButton");
        logoutButton.addActionListener(e -> logout());
        buttonPanel.add(logoutButton);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel = new JLabel(" Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        JLabel dbStatus = new JLabel("Database: MySQL Connected ");
        dbStatus.setForeground(new Color(0, 150, 0));
        statusPanel.add(dbStatus, BorderLayout.EAST);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(statusPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));

        card.add(titleLabel);
        card.add(valueLabel);
        return card;
    }

    private void updateDashboard() {
        List<Expense> expenses = tableModel.getExpenses();
        double total = expenses.stream().mapToDouble(e -> e.getAmount().doubleValue()).sum();
        totalSpendingLabel.setText("€" + df.format(total));

        double budget = currentUser.getMonthlyBudget().doubleValue();
        budgetValueLabel.setText("€" + df.format(budget));

        if (total > budget) {
            budgetCard.setBackground(new Color(255, 230, 230));
            budgetValueLabel.setForeground(Color.RED);
        } else {
            budgetCard.setBackground(new Color(235, 255, 235));
            budgetValueLabel.setForeground(new Color(0, 100, 0));
        }

        Map<String, Long> categoryCounts = expenses.stream()
                .filter(e -> e.getCategory() != null)
                .collect(Collectors.groupingBy(e -> e.getCategory().getName(), Collectors.counting()));

        String topCategory = categoryCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        topCategoryLabel.setText(topCategory);
    }

    private void showSetBudgetDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter your monthly budget (€):",
                currentUser.getMonthlyBudget());
        if (input != null && !input.isEmpty()) {
            try {
                BigDecimal newBudget = new BigDecimal(input);
                currentUser.setMonthlyBudget(newBudget);
                userService.updateUser(currentUser);
                updateDashboard();
                JOptionPane.showMessageDialog(this, "Monthly budget updated!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddExpenseDialog() {
        AddExpenseView dialog = new AddExpenseView(this, expenseService, categoryService, currentUser,
                this::refreshExpenses);
        dialog.setVisible(true);
    }

    private void refreshExpenses() {
        SwingUtilities.invokeLater(() -> {
            List<Expense> expenses = expenseService.getExpensesByUser(currentUser);
            tableModel.setExpenses(expenses);
            updateDashboard();
        });
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setName("logoutItem");
        logoutItem.addActionListener(e -> logout());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        JMenuItem analyticsItem = new JMenuItem("View Statistics");
        analyticsItem.setName("analyticsItem");
        analyticsItem.addActionListener(e -> new StatisticsView(this, tableModel.getExpenses()).setVisible(true));

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu analyticsMenu = new JMenu("Analytics");
        analyticsMenu.add(analyticsItem);

        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem budgetItem = new JMenuItem("Set Monthly Budget");
        budgetItem.setName("budgetItem");
        budgetItem.addActionListener(e -> showSetBudgetDialog());
        settingsMenu.add(budgetItem);

        menuBar.add(fileMenu);
        menuBar.add(analyticsMenu);
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);
    }

    private void logout() {
        dispose();
        new LoginView(userService, expenseService, categoryService).setVisible(true);
    }
}
