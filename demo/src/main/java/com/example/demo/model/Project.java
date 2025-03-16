package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    // L'administrateur du projet (User qui l'a créé)
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    // Liste des membres avec rôles
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectMember> projectMembers = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();


    public Project(String name, String description, LocalDate startDate, User admin) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.admin = admin;
    }

    public void addMember(User user, ProjectRole role) {
        this.projectMembers.add(new ProjectMember(this, user, role));
    }
}
