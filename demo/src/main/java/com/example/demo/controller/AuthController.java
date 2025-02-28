package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.service.AuthService;
import java.util.Map;
import com.example.demo.model.User;;

@RestController
@RequestMapping("/api/auth") // ✅ Route mise à jour avec /api/auth
public class AuthController {
    
        @Autowired
        private AuthService authService;

        @PostMapping("/signup")
        public ResponseEntity<String> register(@RequestBody SignupRequest request) {
            return ResponseEntity.ok(authService.registerUser(request));
        }

        @PostMapping("/signin")
        public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
            User user = authService.loginUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(Map.of("message", "Connexion réussie", "token", user.getEmail(), "user_id", String.valueOf(user.getId())));
        }

    
}
