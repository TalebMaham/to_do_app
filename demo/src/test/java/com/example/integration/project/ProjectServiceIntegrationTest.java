package com.example.integration.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.DemoApplication;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectMemberRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskHistoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProjectService;

@SpringBootTest(classes = DemoApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProjectServiceIntegrationTest {

    @Autowired private ProjectService projectService;
    @Autowired private TaskHistoryRepository taskHistoryRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        // 💥 Supprimer dans l’ordre inverse des dépendances
        taskHistoryRepository.deleteAll(); // dépend de Task
        projectMemberRepository.deleteAll(); 
        taskRepository.deleteAll();        // dépend de Project et User
        projectRepository.deleteAll();     // dépend de User
        userRepository.deleteAll();        // indépendant (en dernier)
    }

    @Test
    void createProject_shouldPersistProjectInDatabase() {
        User admin = new User("admin", "admin@projet.com", "secret");
        admin = userRepository.save(admin);

        Project created = projectService.createProject(
                "Mon projet",
                "Une super idée",
                "2025-03-21",
                admin.getId()
        );

        assertNotNull(created.getId());
        assertEquals("Mon projet", created.getName());
        assertEquals("Une super idée", created.getDescription());
        assertEquals(LocalDate.of(2025, 3, 21), created.getStartDate());
        assertEquals(admin.getId(), created.getAdmin().getId());
    }

    @Test
    void createProject_shouldFail_whenAdminDoesNotExist() {
        assertThrows(RuntimeException.class, () ->
                projectService.createProject("X", "Y", "2025-03-21", 123L));
    }

    @Test
    void getAllProjects_shouldReturnProjectsList() {
        assertEquals(0, projectService.getAllProjects().size());

        User admin = userRepository.save(new User("admin", "mail@mail.com", "pass"));
        projectRepository.save(new Project("Projet", "desc", LocalDate.now(), admin));

        assertEquals(1, projectService.getAllProjects().size());
    }

    @Test
    void getProjectsByAdmin_shouldReturnOnlyAdminProjects() {
        User admin = userRepository.save(new User("admin", "mail@mail.com", "pass"));
        projectRepository.save(new Project("AdminProject", "desc", LocalDate.now(), admin));

        assertEquals(1, projectService.getProjectsByAdmin(admin.getId()).size());
    }

    @Test
    void getProjectsByUser_shouldIncludeAdminProjects() {
        User admin = userRepository.save(new User("admin", "mail@mail.com", "pass"));
        projectRepository.save(new Project("AdminProject", "desc", LocalDate.now(), admin));

        assertEquals(1, projectService.getProjectsByUser(admin.getId()).size());
    }

    @Test
    void getProjectById_shouldReturnProject_whenExists() {
        User admin = userRepository.save(new User("admin", "mail@mail.com", "pass"));
        Project p = projectRepository.save(new Project("Projet", "desc", LocalDate.now(), admin));

        assertEquals(true, projectService.getProjectById(p.getId()).isPresent());
    }

    @Test
    void getProjectById_shouldReturnEmpty_whenNotFound() {
        assertEquals(false, projectService.getProjectById(999L).isPresent());
    }

    @Test
    void deleteProject_shouldDelete_whenProjectExists() {
        User admin = userRepository.save(new User("admin", "mail@mail.com", "pass"));
        Project p = projectRepository.save(new Project("Projet", "desc", LocalDate.now(), admin));

        boolean deleted = projectService.deleteProjectById(p.getId());
        assertEquals(true, deleted);
    }

    @Test
    void deleteProject_shouldReturnFalse_whenNotFound() {
        boolean deleted = projectService.deleteProjectById(999L);
        assertEquals(false, deleted);
    }

    @Test
    void assignRole_shouldThrow_ifUserOrProjectNotFound() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            projectService.assignRole(999L, 999L, com.example.demo.model.ProjectRole.ADMIN));
        assertEquals("Projet non trouvé", ex.getMessage());
    }

    @Test
    void addMemberAndGetMembers_shouldWorkCorrectly() {
        User admin = userRepository.save(new User("admin", "admin@p.com", "pwd"));
        User user = userRepository.save(new User("member", "m@m.com", "pwd"));
        Project project = projectRepository.save(new Project("P", "d", LocalDate.now(), admin));

        projectService.addMemberToProject(project.getId(), user.getEmail(), null);

        var members = projectService.getProjectMembers(project.getId());
        assertEquals(1, members.size());
        assertEquals("member", members.get(0).getUsername());
    }







}

