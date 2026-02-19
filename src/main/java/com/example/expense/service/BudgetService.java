package com.example.expense.service;

import com.example.expense.model.Expense;
import com.example.expense.model.User;
import java.math.BigDecimal;
import java.util.List;

public class BudgetService {

    public boolean isBudgetExceeded(User user, List<Expense> expenses) {
        if (user.getMonthlyBudget() == null) {
            return false;
        }

        BigDecimal totalSpending = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalSpending.compareTo(user.getMonthlyBudget()) > 0;
    }

    public BigDecimal getRemainingBudget(User user, List<Expense> expenses) {
        if (user.getMonthlyBudget() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalSpending = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return user.getMonthlyBudget().subtract(totalSpending);
    }
}
