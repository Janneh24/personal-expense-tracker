package com.example.expense.view;

import com.example.expense.model.Expense;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ExpenseTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Date", "Category", "Description", "Amount"};
    private final List<Expense> expenses = new ArrayList<>();

    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }
    
    public void setExpenses(List<Expense> expenses) {
        this.expenses.clear();
        this.expenses.addAll(expenses);
        fireTableDataChanged();
    }
    
    public Expense getExpenseAt(int rowIndex) {
        return expenses.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return expenses.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Expense expense = expenses.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return expense.getDate();
            case 1:
                return expense.getCategory() != null ? expense.getCategory().getName() : "N/A";
            case 2:
                return expense.getDescription();
            case 3:
                return expense.getAmount();
            default:
                return null;
        }
    }
}
