package com.example.integration.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.demo.DemoApplication;
import com.example.demo.dto.SignupRequest;
import com.example.demo.exceptions.user_exceptions.AuthenticationException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;

@SpringBootTest(classes = DemoApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_shouldPersistUser() {
        SignupRequest request = new SignupRequest("user2", "user2@example.com", "password");

        String result = authService.registerUser(request);

        assertEquals("Utilisateur inscrit avec succès", result);
        assertTrue(userRepository.existsByUsername("user2"));
        assertTrue(userRepository.existsByEmail("user2@example.com"));
    }

        @Test
    void loginUser_shouldSucceed_withValidCredentials() {
        User user = new User("testuser", "test@email.com", "123456");
        userRepository.save(user);

        User result = authService.loginUser("testuser", "123456");

        assertEquals("testuser", result.getUsername());
    }

    @Test
    void loginUser_shouldFail_withWrongPassword() {
        userRepository.save(new User("sidi", "email", "realpass"));

        assertThrows(AuthenticationException.class, () ->
                authService.loginUser("sidi", "wrongpass"));
    }

    @Test
    void loginUser_shouldFail_whenUsernameDoesNotExist() {
        assertThrows(AuthenticationException.class, () ->
                authService.loginUser("unknown", "any"));
    }
}

