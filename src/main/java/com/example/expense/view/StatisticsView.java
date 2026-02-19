package com.example.expense.view;

import com.example.expense.model.Expense;
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

public class StatisticsView extends JDialog {

    public StatisticsView(Frame owner, List<Expense> expenses) {
        super(owner, "Spending Statistics", true);
        setSize(1000, 600);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(1, 2));

        add(createPieChartPanel(expenses));
        add(createBarChartPanel(expenses));
    }

    private JPanel createPieChartPanel(List<Expense> expenses) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        Map<String, Double> categoryTotals = expenses.stream()
                .filter(e -> e.getCategory() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().getName(),
                        Collectors.summingDouble(e -> e.getAmount().doubleValue())));

        categoryTotals.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Expenses by Category",
                dataset,
                true, true, false);

        return new ChartPanel(chart);
    }

    private JPanel createBarChartPanel(List<Expense> expenses) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Double> categoryTotals = expenses.stream()
                .filter(e -> e.getCategory() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().getName(),
                        Collectors.summingDouble(e -> e.getAmount().doubleValue())));

        categoryTotals.forEach((cat, total) -> dataset.addValue(total, "Amount", cat));

        JFreeChart chart = ChartFactory.createBarChart(
                "Category Comparison",
                "Category",
                "Amount (€)",
                dataset);

        return new ChartPanel(chart);
    }
}
