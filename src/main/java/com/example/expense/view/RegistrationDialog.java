package com.example.expense.view;

import com.example.expense.model.Role;
import com.example.expense.service.UserService;
import javax.swing.*;
import java.awt.*;

public class RegistrationDialog extends JDialog {
    private final UserService userService;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private boolean successful = false;

    public RegistrationDialog(Frame owner, UserService userService) {
        super(owner, "Register", true);
        this.userService = userService;

        setSize(300, 200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        usernameField = new JTextField();
        usernameField.setName("regUsername");
        passwordField = new JPasswordField();
        passwordField.setName("regPassword");

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        okButton.setName("regOkButton");
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setName("regCancelButton");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> handleRegister());
        cancelButton.addActionListener(e -> dispose());
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty");
            return;
        }

        try {
            userService.registerUser(username, password, Role.USER);
            JOptionPane.showMessageDialog(this, "Registration successful! use your new credentials to login.");
            successful = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + e.getMessage());
        }
    }

    public boolean isSuccessful() {
        return successful;
    }
}
