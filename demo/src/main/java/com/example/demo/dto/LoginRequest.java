package com.example.demo.dto;

public class LoginRequest {
    private String username;
    private String password;

    // Getters et Setters
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
