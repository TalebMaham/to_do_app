package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project_members")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role; // ADMIN, MEMBER, OBSERVER

    public ProjectMember(Project project, User user, ProjectRole role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }

    public ProjectMember() {}

    // Getters et Setters

    public User getUser() {
        return user;
    }

    public ProjectRole getRole() {
        return role;
    }
}
