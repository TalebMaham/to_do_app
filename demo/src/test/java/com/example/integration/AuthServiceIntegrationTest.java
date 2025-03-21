package com.example.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.demo.DemoApplication;
import com.example.demo.dto.SignupRequest;
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
}

