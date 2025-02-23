package com.example.demo;

import com.example.demo.controller.AuthController;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testInscriptionUtilisateur_Success() throws Exception {
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);

        String userJson = """
        {
            "username": "testuser",
            "email": "test@mail.com",
            "password": "password123"
        }
        """;

        mockMvc.perform(post("/api/auth/signup") // ✅ Route mise à jour
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Utilisateur inscrit avec succès."));
    }

    @Test
    public void testInscriptionUtilisateur_UsernameDejaPris() throws Exception {
        when(userRepository.existsByUsername(any())).thenReturn(true);

        String userJson = """
        {
            "username": "testuser",
            "email": "test@mail.com",
            "password": "password123"
        }
        """;

        mockMvc.perform(post("/api/auth/signup") // ✅ Route mise à jour
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le nom d'utilisateur est déjà pris."));
    }

    @Test
    public void testInscriptionUtilisateur_EmailDejaUtilise() throws Exception {
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        String userJson = """
        {
            "username": "testuser",
            "email": "test@mail.com",
            "password": "password123"
        }
        """;

        mockMvc.perform(post("/api/auth/signup") // ✅ Route mise à jour
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'email est déjà utilisé."));
    }
}
