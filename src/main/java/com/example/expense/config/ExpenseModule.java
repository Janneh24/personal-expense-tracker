package com.example.expense.config;

import com.example.expense.repository.ExpenseRepository;
import com.example.expense.repository.UserRepository;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.service.ExpenseService;
import com.example.expense.service.UserService;
import com.example.expense.service.CategoryService;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ExpenseModule extends AbstractModule {

    @Override
    protected void configure() {
        // Bind repositories and services
        bind(ExpenseRepository.class).in(Singleton.class);
        bind(UserRepository.class).in(Singleton.class);
        bind(CategoryRepository.class).in(Singleton.class);

        bind(ExpenseService.class).in(Singleton.class);
        bind(UserService.class).in(Singleton.class);
        bind(CategoryService.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    EntityManagerFactory provideEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("expense-tracker-unit");
    }

    @Provides
    @Singleton
    EntityManager provideEntityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }
}
