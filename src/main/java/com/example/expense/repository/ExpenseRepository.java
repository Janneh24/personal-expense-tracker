package com.example.expense.repository;

import com.example.expense.model.Expense;
import com.example.expense.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ExpenseRepository extends BaseRepository<Expense> {

    @jakarta.inject.Inject
    public ExpenseRepository(EntityManager entityManager) {
        super(entityManager, Expense.class);
    }

    public List<Expense> findByUser(User user) {
        String jpql = "SELECT e FROM Expense e WHERE e.user = :user";
        TypedQuery<Expense> query = entityManager.createQuery(jpql, Expense.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public List<Expense> findByUserAndDateBetween(User user, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        String jpql = "SELECT e FROM Expense e WHERE e.user = :user AND e.date BETWEEN :startDate AND :endDate";
        TypedQuery<Expense> query = entityManager.createQuery(jpql, Expense.class);
        query.setParameter("user", user);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
}
