package com.example.demo.dto;

import com.example.demo.model.ProjectRole;

public class ProjectMemberDTO {
    private Long userId;
    private String username;
    private String email;
    private ProjectRole role;

    public ProjectMemberDTO(Long userId, String username, String email, ProjectRole role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getters
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public ProjectRole getRole() { return role; }
}
