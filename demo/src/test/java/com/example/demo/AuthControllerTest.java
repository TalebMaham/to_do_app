package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.demo.controller.AuthController;
import com.example.demo.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class) // ✅ Charge uniquement le contrôleur pour éviter les erreurs de contexte
public class AuthControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockitoBean // ✅ Mock du service AuthService
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // ✅ Vérifie que le mock est bien configuré avant chaque test
        when(authService.registerUser(any())).thenReturn("Utilisateur inscrit avec succès");
    }

    @Test
    public void testInscriptionUtilisateur() throws Exception {
        String userJson = """
        {
            "username": "testuser",
            "email": "test@mail.com",
            "password": "password123"
        }
        """; 

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Utilisateur inscrit avec succès"));
    }
}
