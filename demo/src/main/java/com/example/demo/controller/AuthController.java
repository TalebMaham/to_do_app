package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.SignupRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth") // ✅ Route mise à jour avec /api/auth
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody SignupRequest signupRequest) {
        Map<String, String> response = new HashMap<>();

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            response.put("error", "Le nom d'utilisateur est déjà pris.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            response.put("error", "L'email est déjà utilisé.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // ⚠️ ATTENTION : Le mot de passe est stocké en clair (moins sécurisé)
        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), signupRequest.getPassword());
        userRepository.save(user);

        response.put("message", "Utilisateur inscrit avec succès.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
