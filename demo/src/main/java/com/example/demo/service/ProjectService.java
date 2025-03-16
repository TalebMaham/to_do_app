package com.example.demo.service;

import com.example.demo.dto.ProjectMemberDTO;
import com.example.demo.model.Project;
import com.example.demo.model.ProjectMember;
import com.example.demo.model.ProjectRole;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectMemberRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    // Création d'un projet avec un admin
    public Project createProject(String name, String description, String startDate, Long adminId) {
        Optional<User> adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            throw new RuntimeException("Admin introuvable avec l'ID : " + adminId);
        }
        User admin = adminOpt.get();
        Project project = new Project(name, description, java.time.LocalDate.parse(startDate), admin);
        return projectRepository.save(project);
    }

    // Récupérer tous les projets
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // Récupérer les projets d'un administrateur spécifique
    public List<Project> getProjectsByAdmin(Long adminId) {
        return projectRepository.findByAdminId(adminId);
    }

     // Récupérer les projets où l'utilisateur est admin ou membre
    public List<Project> getProjectsByUser(Long userId) {
        List<Project> adminProjects = projectRepository.findByAdminId(userId);
        List<Project> memberProjects = projectRepository.findByUserIsMember(userId);

        // Fusionner les listes sans doublons
        Set<Project> allProjects = new HashSet<>(adminProjects);
        allProjects.addAll(memberProjects);

        return List.copyOf(allProjects);
    }



    @Transactional
    public Project addMemberToProject(Long projectId, String userEmail, ProjectRole role) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet introuvable avec l'ID : " + projectId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'e-mail : " + userEmail));

        // Vérifier si l'utilisateur est déjà membre du projet
        boolean alreadyMember = projectMemberRepository.existsByProjectAndUser(project, user);
        if (alreadyMember) {
            throw new RuntimeException("L'utilisateur est déjà membre du projet.");
        }

        // Si aucun rôle n'est spécifié, attribuer "MEMBER" par défaut
        ProjectRole assignedRole = (role != null) ? role : ProjectRole.MEMBER;

        // Ajouter l'utilisateur avec le rôle déterminé
        ProjectMember projectMember = new ProjectMember(project, user, assignedRole);
        projectMemberRepository.save(projectMember);

        return project;
    }

    public List<ProjectMemberDTO> getProjectMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet introuvable avec l'ID : " + projectId));

        // Récupérer tous les membres du projet avec leurs rôles
        return projectMemberRepository.findByProject(project)
                .stream()
                .map(pm -> new ProjectMemberDTO(
                        pm.getUser().getId(),
                        pm.getUser().getUsername(),
                        pm.getUser().getEmail(),
                        pm.getRole()))
                .collect(Collectors.toList());
    }

    public Optional<Project> getProjectById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    public boolean deleteProjectById(Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent()) {
            projectRepository.deleteById(projectId);
            return true;
        }
        return false;
    }


    @Transactional
    public void assignRole(Long projectId, Long userId, ProjectRole role) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        ProjectMember projectMember = new ProjectMember(project, user, role);
        projectMemberRepository.save(projectMember);
    }
    
}
