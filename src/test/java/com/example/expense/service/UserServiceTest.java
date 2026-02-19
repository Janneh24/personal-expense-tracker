package com.example.expense.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.expense.model.Role;
import com.example.expense.model.User;
import com.example.expense.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void authenticate_shouldReturnUser_whenCredentialsValid() {
        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw("pass", org.mindrot.jbcrypt.BCrypt.gensalt());
        User user = new User(1L, "test", hashed, Role.USER);
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        Optional<User> result = userService.authenticate("test", "pass");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("test");
    }

    @Test
    void authenticate_shouldReturnEmpty_whenPasswordInvalid() {
        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw("pass", org.mindrot.jbcrypt.BCrypt.gensalt());
        User user = new User(1L, "test", hashed, Role.USER);
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        Optional<User> result = userService.authenticate("test", "wrong");

        assertThat(result).isEmpty();
    }

    @Test
    void authenticate_shouldReturnEmpty_whenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = userService.authenticate("unknown", "pass");

        assertThat(result).isEmpty();
    }

    @Test
    void registerUser_shouldSaveUser_whenUsernameAvailable() {
        when(userRepository.findByUsername("new")).thenReturn(Optional.empty());
        User savedUser = new User(1L, "new", "pass", Role.USER);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser("new", "pass", Role.USER);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_shouldThrow_whenUsernameExists() {
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.registerUser("existing", "pass", Role.USER))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        User user = new User();
        when(userRepository.update(user)).thenReturn(user);
        User result = userService.updateUser(user);
        assertThat(result).isEqualTo(user);
        verify(userRepository).update(user);
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(new User()));
        assertThat(userService.getAllUsers()).hasSize(1);
    }

    @Test
    void setUserStatus_shouldUpdateIfFound() {
        Long id = 1L;
        User user = new User();
        user.setEnabled(true);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.setUserStatus(id, false);

        assertThat(user.isEnabled()).isFalse();
        verify(userRepository).update(user);
    }

    @Test
    void resetPassword_shouldHashAndVerify() {
        Long id = 1L;
        User user = new User();
        user.setPassword("old");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.resetPassword(id, "newPass");

        assertThat(org.mindrot.jbcrypt.BCrypt.checkpw("newPass", user.getPassword())).isTrue();
        verify(userRepository).update(user);
    }
}
