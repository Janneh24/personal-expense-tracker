package com.example.expense.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

@Testcontainers
public abstract class AbstractRepositoryIT {

    @Container
    protected static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.2.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    protected static EntityManagerFactory emf;
    protected EntityManager entityManager;

    @BeforeAll
    static void setUpAll() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", mysql.getJdbcUrl());
        properties.put("jakarta.persistence.jdbc.user", mysql.getUsername());
        properties.put("jakarta.persistence.jdbc.password", mysql.getPassword());
        properties.put("jakarta.persistence.jdbc.driver", mysql.getDriverClassName());
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        emf = Persistence.createEntityManagerFactory("expense-tracker-unit", properties);
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    void setUp() {
        entityManager = emf.createEntityManager();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null) {
            entityManager.close();
        }
    }
}
