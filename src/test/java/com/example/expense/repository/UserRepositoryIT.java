package com.example.expense.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.expense.model.Role;
import com.example.expense.model.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserRepositoryIT extends AbstractRepositoryIT {

    private UserRepository userRepository;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        userRepository = new UserRepository(entityManager);
    }

    @Test
    void saveAndFindById() {
        User user = new User(null, "testuser", "password", Role.USER);
        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findByUsername() {
        User user = new User(null, "unique", "pass", Role.USER);
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("unique");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("unique");
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        assertThat(found).isEmpty();
    }
}
