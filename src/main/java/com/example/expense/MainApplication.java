package com.example.expense;

import com.example.expense.config.ExpenseModule;
import com.example.expense.service.CategoryService;
import com.example.expense.service.ExpenseService;
import com.example.expense.service.UserService;
import com.formdev.flatlaf.FlatLightLaf;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.swing.SwingUtilities;

public class MainApplication {

    public static void main(String[] args) {
        // Setup FlatLaf for modern look
        try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        Injector injector = Guice.createInjector(new ExpenseModule());
        ExpenseService expenseService = injector.getInstance(ExpenseService.class);
        UserService userService = injector.getInstance(UserService.class);
        CategoryService categoryService = injector.getInstance(CategoryService.class);

        // Seed initial data
        seedData(userService, categoryService);

        SwingUtilities.invokeLater(() -> {
            com.example.expense.view.LoginView loginView = new com.example.expense.view.LoginView(userService,
                    expenseService, categoryService);
            loginView.setVisible(true);
        });
    }

    private static void seedData(UserService userService, CategoryService categoryService) {
        // Seed Default Categories
        try {
            categoryService.seedDefaultCategories();
            System.out.println("Seeded default categories");
        } catch (Exception e) {
            System.err.println("Failed to seed categories: " + e.getMessage());
        }

        // Seed Admin
        try {
            userService.registerUser("admin", "admin", com.example.expense.model.Role.ADMIN);
            System.out.println("Seeded admin user");
        } catch (Exception e) {
            System.out.println("Admin user already exists or seeding failed: " + e.getMessage());
        }

        // Seed Regular User
        try {
            userService.registerUser("user", "user", com.example.expense.model.Role.USER);
            System.out.println("Seeded regular user");
        } catch (Exception e) {
            System.out.println("Regular user already exists or seeding failed: " + e.getMessage());
        }
    }
}
