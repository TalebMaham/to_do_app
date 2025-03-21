package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.service.AuthService;
import java.util.Map;
import java.util.UUID;

import com.example.demo.model.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> register(@RequestBody SignupRequest request) {
        String message = authService.registerUser(request);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        User user = authService.loginUser(request.getUsername(), request.getPassword());
        String token = UUID.randomUUID().toString(); // Simule un token
        return ResponseEntity.ok(Map.of(
                "message", "Connexion réussie",
                "token", token,
                "user_id", String.valueOf(user.getId()),
                "user_name", user.getUsername()
        ));
    }
}
