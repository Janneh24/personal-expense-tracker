package com.example.expense.service;

import com.example.expense.model.Role;
import com.example.expense.model.User;
import com.example.expense.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import jakarta.inject.Inject;

public class UserService {

    private final UserRepository userRepository;

    @Inject
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public User registerUser(String username, String password, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
        User newUser = new User(null, username, hashedPassword, role);
        return userRepository.save(newUser);
    }

    public User updateUser(User user) {
        return userRepository.update(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void setUserStatus(Long userId, boolean enabled) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setEnabled(enabled);
            userRepository.update(user);
        });
    }

    public void resetPassword(Long userId, String newPassword) {
        userRepository.findById(userId).ifPresent(user -> {
            String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(newPassword,
                    org.mindrot.jbcrypt.BCrypt.gensalt());
            user.setPassword(hashedPassword);
            userRepository.update(user);
        });
    }
}
