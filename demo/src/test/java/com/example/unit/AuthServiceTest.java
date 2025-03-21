package com.example.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.SignupRequest;
import com.example.demo.exceptions.user_exceptions.EmailAlreadyUsedException;
import com.example.demo.exceptions.user_exceptions.UsernameAlreadyTakenException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_shouldSucceed_whenUsernameAndEmailAreUnique() {
        SignupRequest request = new SignupRequest("user1", "user1@example.com", "password");

        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(false);

        String result = authService.registerUser(request);

        assertEquals("Utilisateur inscrit avec succès", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_shouldThrow_whenUsernameExists() {
        SignupRequest request = new SignupRequest("user1", "user1@example.com", "password");

        when(userRepository.existsByUsername("user1")).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class, () -> authService.registerUser(request));
    }

    @Test
    void registerUser_shouldThrow_whenEmailExists() {
        SignupRequest request = new SignupRequest("user1", "user1@example.com", "password");

        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> authService.registerUser(request));
    }
}
