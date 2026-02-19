package com.example.expense.view;

import com.example.expense.model.Role;
import com.example.expense.model.User;
import com.example.expense.service.ExpenseService;
import com.example.expense.service.UserService;
import com.example.expense.service.CategoryService;
import jakarta.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginView extends JFrame {

    private final UserService userService;
    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<Role> roleComboBox;

    @Inject
    public LoginView(UserService userService, ExpenseService expenseService,
            CategoryService categoryService) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.categoryService = categoryService;

        setTitle("Login - Expense Tracker");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel outerPanel = new JPanel(new GridBagLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        mainPanel.setPreferredSize(new Dimension(350, 450));
        mainPanel.setMinimumSize(new Dimension(350, 450));
        mainPanel.setMaximumSize(new Dimension(350, 450));

        JLabel headerLabel = new JLabel("Welcome");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField = new JTextField();
        usernameField.setName("username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel passLabel = new JLabel("Password");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField();
        passwordField.setName("password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel roleLabel = new JLabel("Login As");
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleComboBox = new JComboBox<>(Role.values());
        roleComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        ((JLabel) roleComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        JButton loginButton = new JButton("Login");
        loginButton.setName("loginButton");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        loginButton.addActionListener(e -> login());

        JButton registerButton = new JButton("Create Account");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        registerButton.addActionListener(e -> showRegisterDialog());

        mainPanel.add(headerLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        mainPanel.add(userLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(usernameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainPanel.add(passLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainPanel.add(roleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(roleComboBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        mainPanel.add(loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(registerButton);

        outerPanel.add(mainPanel);
        add(outerPanel);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        Optional<User> userOpt = userService.authenticate(username, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Role selectedRole = (Role) roleComboBox.getSelectedItem();

            if (user.getRole() != selectedRole) {
                JOptionPane.showMessageDialog(this, "Invalid Role for this user.", "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!user.isEnabled()) {
                JOptionPane.showMessageDialog(this, "Your account has been disabled. Please contact admin.",
                        "Account Disabled", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (user.getRole() == Role.ADMIN) {
                new AdminDashboardView(userService, expenseService, categoryService, user).setVisible(true);
            } else {
                new MainView(userService, expenseService, categoryService, user).setVisible(true);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegisterDialog() {
        RegistrationDialog dialog = new RegistrationDialog(this, userService);
        dialog.setVisible(true);
    }
}
