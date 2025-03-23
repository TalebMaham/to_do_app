package com.example.functionnal.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.DemoApplication;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.exceptions.user_exceptions.UsernameAlreadyTakenException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertTrue;



@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void signup_shouldReturnSuccessMessage() throws Exception {
        SignupRequest request = new SignupRequest("newuser", "newuser@example.com", "password");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Utilisateur inscrit avec succès"));
    }

    @Test
    void signup_shouldReturnError_whenUsernameTaken() throws Exception {
        userRepository.save(new User("existing", "test@email.com", "pwd"));

        SignupRequest request = new SignupRequest("existing", "new@email.com", "pass");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()) // Ou 409 si tu as une exception personnalisée avec handler
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UsernameAlreadyTakenException));
    }

    @Test
    void signin_shouldReturnToken_whenCredentialsAreCorrect() throws Exception {
    // Arrange : on enregistre un user dans la base
    User user = new User("loginuser", "login@email.com", "pass123");
    userRepository.save(user);

    LoginRequest request = new LoginRequest("loginuser", "pass123");

    // Act & Assert
    mockMvc.perform(post("/api/auth/signin") // adapte le chemin si nécessaire
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Connexion réussie"))
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.user_id").value(user.getId().toString()))
            .andExpect(jsonPath("$.user_name").value("loginuser"));
    }


    @Test
    void signin_shouldReturnError_whenUsernameNotFound() throws Exception {
        LoginRequest request = new LoginRequest("unknown", "pass");

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Nom d'utilisateur incorrect"));
    }

    @Test
    void signin_shouldReturnError_whenPasswordIsWrong() throws Exception {
        userRepository.save(new User("loginuser", "login@email.com", "realpass"));

        LoginRequest request = new LoginRequest("loginuser", "wrongpass");

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Mot de passe incorrect"));
    }





}
