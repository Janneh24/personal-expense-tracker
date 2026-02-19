package com.example.expense.view;

import com.example.expense.model.Expense;
import com.example.expense.model.User;
import com.example.expense.model.Category;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;

public class AdminStatisticsView extends JDialog {

    public AdminStatisticsView(Frame owner, List<Expense> allExpenses, List<User> allUsers) {
        super(owner, "Platform Global Insights", true);
        setSize(1200, 600);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(1, 3));

        add(createCategoryPieChart(allExpenses));
        add(createUserSpendingBarChart(allExpenses));
        add(createUserRolePieChart(allUsers));
    }

    private JPanel createCategoryPieChart(List<Expense> expenses) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Double> totals = expenses.stream()
                .filter(e -> e.getCategory() != null)
                .collect(Collectors.groupingBy(e -> e.getCategory().getName(),
                        Collectors.summingDouble(e -> e.getAmount().doubleValue())));
        totals.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart("Global Spending by Category", dataset, true, true, false);
        return new ChartPanel(chart);
    }

    private JPanel createUserSpendingBarChart(List<Expense> expenses) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> totals = expenses.stream()
                .collect(Collectors.groupingBy(e -> e.getUser().getUsername(),
                        Collectors.summingDouble(e -> e.getAmount().doubleValue())));
        totals.forEach((user, total) -> dataset.addValue(total, "Spending", user));

        JFreeChart chart = ChartFactory.createBarChart("Spending per User", "User", "Amount (€)", dataset);
        return new ChartPanel(chart);
    }

    private JPanel createUserRolePieChart(List<User> users) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Long> counts = users.stream()
                .collect(Collectors.groupingBy(u -> u.getRole().toString(), Collectors.counting()));
        counts.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart("User Role Distribution", dataset, true, true, false);
        return new ChartPanel(chart);
    }
}
