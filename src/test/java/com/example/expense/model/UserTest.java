package com.example.expense.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void testUserProperties() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setRole(Role.ADMIN);
        user.setEnabled(false);
        user.setMonthlyBudget(new BigDecimal("500.00"));

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testUser");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.isEnabled()).isFalse();
        assertThat(user.getMonthlyBudget()).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    void testConstructors() {
        User user1 = new User(1L, "u1", "p1", Role.USER);
        assertThat(user1.getUsername()).isEqualTo("u1");
        assertThat(user1.getMonthlyBudget()).isEqualTo(new BigDecimal("1000.00"));

        User user2 = new User(2L, "u2", "p2", Role.ADMIN, new BigDecimal("2000.00"));
        assertThat(user2.getMonthlyBudget()).isEqualTo(new BigDecimal("2000.00"));
    }

    @Test
    void testToString() {
        User user = new User(1L, "testUser", "password", Role.USER);
        assertThat(user.toString()).contains("testUser");
        assertThat(user.toString()).contains("USER");
    }
}
