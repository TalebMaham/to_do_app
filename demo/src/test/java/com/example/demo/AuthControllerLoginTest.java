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
public class AuthControllerLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    // @Test
    // public void testConnexionUtilisateur_Success() throws Exception {
    //     when(userRepository.existsByEmail(any())).thenReturn(true);

    //     String loginJson = """
    //     {
    //         "email": "test@mail.com",
    //         "password": "password123"
    //     }
    //     """;

    //     mockMvc.perform(post("/api/auth/signin")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(loginJson))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.message").value("Connexion réussie."));
    // }

    // @Test
    // public void testConnexionUtilisateur_EmailInexistant() throws Exception {
    //     when(userRepository.existsByEmail(any())).thenReturn(false);

    //     String loginJson = """
    //     {
    //         "email": "test@mail.com",
    //         "password": "password123"
    //     }
    //     """;

    //     mockMvc.perform(post("/api/auth/signin")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(loginJson))
    //             .andExpect(status().isUnauthorized())
    //             .andExpect(jsonPath("$.error").value("Email ou mot de passe incorrect."));
    // }

    // @Test
    // public void testConnexionUtilisateur_MotDePasseIncorrect() throws Exception {
    //     when(userRepository.existsByEmail(any())).thenReturn(true);

    //     String loginJson = """
    //     {
    //         "email": "test@mail.com",
    //         "password": "wrongpassword"
    //     }
    //     """;

    //     mockMvc.perform(post("/api/auth/signin")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(loginJson))
    //             .andExpect(status().isUnauthorized())
    //             .andExpect(jsonPath("$.error").value("Email ou mot de passe incorrect."));
    // }
}
