package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.demo.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties") // ✅ Charge la config H2 pour les tests
public class AuthControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockitoBean // ✅ Utilisation de la nouvelle annotation recommandée
    private AuthService authService;

    @Test
    public void testInscriptionUtilisateur() throws Exception {
        when(authService.registerUser(any())).thenReturn("Utilisateur inscrit avec succès"); // ✅ Mock du service

        String userJson = """
        {
            "username": "testuser",
            "email": "test@mail.com",
            "password": "password123"
        }
        """; // ✅ Utilisation de texte multi-lignes pour plus de lisibilité

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Utilisateur inscrit avec succès")); // ✅ Vérification du message JSON retourné
    }
}
