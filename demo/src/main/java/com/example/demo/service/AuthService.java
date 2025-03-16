package com.example.demo.service;

import com.example.demo.dto.SignupRequest;
import com.example.demo.exceptions.user_exceptions.AuthenticationException;
import com.example.demo.exceptions.user_exceptions.EmailAlreadyUsedException;
import com.example.demo.exceptions.user_exceptions.UsernameAlreadyTakenException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

   
    @Autowired
    private UserRepository userRepository;

    public String registerUser(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyTakenException("Le nom d'utilisateur est déjà pris.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException("L'email est déjà utilisé.");
        }

        // Garder le mot de passe en clair (⚠️ Non recommandé)
        User user = new User(request.getUsername(), request.getEmail(), request.getPassword());
        userRepository.save(user);

        return "Utilisateur inscrit avec succès";
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Nom d'utilisateur incorrect"));

        // Comparaison en clair (⚠️ Non sécurisé)
        if (!user.getPassword().equals(password)) {
            throw new AuthenticationException("Mot de passe incorrect");
        }

        return user;
    }
}
