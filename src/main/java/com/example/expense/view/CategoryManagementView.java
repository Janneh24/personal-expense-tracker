package com.example.expense.view;

import com.example.expense.model.Category;
import com.example.expense.service.CategoryService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoryManagementView extends JDialog {
    private final CategoryService categoryService;
    private JTable categoryTable;
    private DefaultTableModel tableModel;

    public CategoryManagementView(Frame owner, CategoryService categoryService) {
        super(owner, "Category Management", true);
        this.categoryService = categoryService;

        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Table
        String[] columnNames = { "ID", "Name", "Type" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setName("categoryTable");
        refreshTable();
        add(new JScrollPane(categoryTable), BorderLayout.CENTER);

        // Controls
        JPanel controlPanel = new JPanel(new FlowLayout());
        JTextField nameField = new JTextField(15);
        nameField.setName("categoryNameField");
        JButton addButton = new JButton("Add Category");
        addButton.setName("addCategoryButton");
        JButton deleteButton = new JButton("Delete");
        deleteButton.setName("deleteCategoryButton");

        controlPanel.add(new JLabel("Name:"));
        controlPanel.add(nameField);
        controlPanel.add(addButton);
        controlPanel.add(deleteButton);

        add(controlPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                try {
                    categoryService.saveCategory(name);
                    nameField.setText("");
                    refreshTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int row = categoryTable.getSelectedRow();
            if (row >= 0) {
                Long id = (Long) tableModel.getValueAt(row, 0);
                boolean isSystem = (boolean) tableModel.getValueAt(row, 2).toString().equals("System");
                if (isSystem) {
                    JOptionPane.showMessageDialog(this, "System categories cannot be deleted.");
                    return;
                }
                categoryService.deleteCategory(id);
                refreshTable();
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Category> categories = categoryService.getAllCategories();
        for (Category c : categories) {
            tableModel.addRow(new Object[] { c.getId(), c.getName(), c.isSystem() ? "System" : "Custom" });
        }
    }
}
