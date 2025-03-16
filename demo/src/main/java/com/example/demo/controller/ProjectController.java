package com.example.demo.controller;

import com.example.demo.model.Project;
import com.example.demo.model.ProjectRole;
import com.example.demo.dto.ProjectMemberDTO;
import com.example.demo.dto.ProjectRequest;
import com.example.demo.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200") // Autorise uniquement Angular local
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Créer un projet
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectRequest request) {
        Project newProject = projectService.createProject(
                request.getName(),
                request.getDescription(),
                request.getStartDate(),
                request.getAdminId()
        );
        return ResponseEntity.ok(newProject);
    }

    @PostMapping("/{projectId}/add-member")
    public ResponseEntity<Project> addMemberToProject(
            @PathVariable Long projectId,
            @RequestParam String email,
            @RequestParam(required = false) ProjectRole role) { // On passe l'email en paramètre

        Project updatedProject = projectService.addMemberToProject(projectId, email, role);
        return ResponseEntity.ok(updatedProject);
    }

    // Récupérer tous les projets
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // Récupérer les projets d'un administrateur spécifique
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<Project>> getProjectsByAdmin(@PathVariable Long adminId) {
        return ResponseEntity.ok(projectService.getProjectsByAdmin(adminId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Project>> getProjectsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(projectService.getProjectsByUser(userId));
    }
    

    // Récupérer un projet par son ID (détail du projet)
    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);
        return project.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
}

    // Supprimer un projet par son ID
@DeleteMapping("/{projectId}")
public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
    boolean deleted = projectService.deleteProjectById(projectId);
    if (deleted) {
        return ResponseEntity.noContent().build(); // 204 No Content si suppression réussie
    } else {
        return ResponseEntity.notFound().build(); // 404 si le projet n'existe pas
    }
}


    @PostMapping("/{projectId}/assign-role")
    public ResponseEntity<String> assignRole(
            @PathVariable Long projectId,
            @RequestParam Long userId,
            @RequestParam ProjectRole role) {

        projectService.assignRole(projectId, userId, role);
        return ResponseEntity.ok("Rôle attribué avec succès !");
    }



    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberDTO>> getProjectMembers(@PathVariable Long projectId) {
        List<ProjectMemberDTO> members = projectService.getProjectMembers(projectId);
        return ResponseEntity.ok(members);
    }

}
