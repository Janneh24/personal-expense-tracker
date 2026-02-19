package com.example.expense.view;

import com.example.expense.model.Role;
import com.example.expense.model.User;
import com.example.expense.model.Expense;
import com.example.expense.service.UserService;
import com.example.expense.service.ExpenseService;
import com.example.expense.service.CategoryService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardView extends JFrame {
    private final UserService userService;
    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final User currentUser;
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JTable expenseTable;
    private ExpenseTableModel expenseTableModel;

    public AdminDashboardView(UserService userService, ExpenseService expenseService, CategoryService categoryService,
            User currentUser) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.currentUser = currentUser;

        setTitle("Admin Dashboard - Personal Expense Tracker");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Admin Control Panel", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("All Expenses", createAllExpensesPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        createMenuBar();
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = { "ID", "Username", "Role", "Status" };
        userTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        loadUsers();
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton toggleStatusButton = new JButton("Disable/Enable");
        toggleStatusButton.setName("toggleStatusButton");
        JButton resetPassButton = new JButton("Reset Password");
        resetPassButton.setName("resetPassButton");
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setName("refreshUsersButton");

        controlPanel.add(toggleStatusButton);
        controlPanel.add(resetPassButton);
        controlPanel.add(refreshButton);
        panel.add(controlPanel, BorderLayout.NORTH);

        JPanel addPanel = new JPanel(new FlowLayout());
        JTextField usernameField = new JTextField(10);
        usernameField.setName("addUserUsername");
        JPasswordField passwordField = new JPasswordField(10);
        passwordField.setName("addUserPassword");
        JComboBox<Role> roleComboBox = new JComboBox<>(Role.values());
        roleComboBox.setName("addUserRole");
        JButton addButton = new JButton("Add User");
        addButton.setName("addUserButton");

        addPanel.add(new JLabel("User:"));
        addPanel.add(usernameField);
        addPanel.add(new JLabel("Pass:"));
        addPanel.add(passwordField);
        addPanel.add(roleComboBox);
        addPanel.add(addButton);
        panel.add(addPanel, BorderLayout.SOUTH);

        toggleStatusButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row >= 0) {
                Long id = (Long) userTableModel.getValueAt(row, 0);
                String status = userTableModel.getValueAt(row, 3).toString();
                userService.setUserStatus(id, status.equals("Disabled"));
                loadUsers();
            }
        });

        resetPassButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row >= 0) {
                Long id = (Long) userTableModel.getValueAt(row, 0);
                String newPass = JOptionPane.showInputDialog(this, "Enter new password:");
                if (newPass != null && !newPass.isEmpty()) {
                    userService.resetPassword(id, newPass);
                    JOptionPane.showMessageDialog(this, "Password reset successfully");
                }
            }
        });

        addButton.addActionListener(e -> {
            try {
                userService.registerUser(usernameField.getText(), new String(passwordField.getPassword()),
                        (Role) roleComboBox.getSelectedItem());
                loadUsers();
                usernameField.setText("");
                passwordField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        refreshButton.addActionListener(e -> loadUsers());

        return panel;
    }

    private JPanel createAllExpensesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        expenseTableModel = new ExpenseTableModel();
        expenseTable = new JTable(expenseTableModel);
        loadExpenses();
        panel.add(new JScrollPane(expenseTable), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh All Expenses");
        refreshButton.setName("refreshExpensesButton");
        refreshButton.addActionListener(e -> loadExpenses());
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUsers() {
        userTableModel.setRowCount(0);
        for (User user : userService.getAllUsers()) {
            userTableModel.addRow(new Object[] {
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    user.isEnabled() ? "Enabled" : "Disabled"
            });
        }
    }

    private void loadExpenses() {
        expenseTableModel.setExpenses(expenseService.getAllExpenses());
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("fileMenu");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setName("logoutItem");
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginView(userService, expenseService, categoryService).setVisible(true);
        });
        fileMenu.add(logoutItem);

        JMenu manageMenu = new JMenu("Manage");
        manageMenu.setName("manageMenu");
        JMenuItem categoryItem = new JMenuItem("System Categories");
        categoryItem.setName("categoryItem");
        categoryItem.addActionListener(e -> new CategoryManagementView(this, categoryService).setVisible(true));
        manageMenu.add(categoryItem);

        JMenu analyticsMenu = new JMenu("Analytics");
        analyticsMenu.setName("analyticsMenu");
        JMenuItem insightItem = new JMenuItem("Platform Insights");
        insightItem.setName("insightItem");
        insightItem.addActionListener(
                e -> new AdminStatisticsView(this, expenseService.getAllExpenses(), userService.getAllUsers())
                        .setVisible(true));
        analyticsMenu.add(insightItem);

        menuBar.add(fileMenu);
        menuBar.add(manageMenu);
        menuBar.add(analyticsMenu);
        setJMenuBar(menuBar);
    }
}
