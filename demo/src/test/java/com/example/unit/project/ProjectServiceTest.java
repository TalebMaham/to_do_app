package com.example.unit.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.ProjectMemberDTO;
import com.example.demo.model.Project;
import com.example.demo.model.ProjectMember;
import com.example.demo.model.ProjectRole;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectMemberRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProjectService;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_shouldReturnProject_whenAdminExists() {
        User admin = new User("admin", "admin@mail.com", "1234");
        admin.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Project project = projectService.createProject("Test", "Description", "2025-03-21", 1L);

        verify(projectRepository).save(projectCaptor.capture());
        Project savedProject = projectCaptor.getValue();

        assertEquals("Test", savedProject.getName());
        assertEquals(LocalDate.of(2025, 3, 21), savedProject.getStartDate());
        assertEquals(admin, savedProject.getAdmin());
    }

    @Test
    void createProject_shouldThrow_whenAdminNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                projectService.createProject("Test", "Description", "2025-03-21", 99L));

        assertTrue(ex.getMessage().contains("Admin introuvable"));
    }

    @Test
    void getAllProjects_shouldReturnList() {
        when(projectRepository.findAll()).thenReturn(List.of(
            new Project("P1", "desc", LocalDate.now(), new User()),
            new Project("P2", "desc", LocalDate.now(), new User())
        ));

        List<Project> projects = projectService.getAllProjects();

        assertEquals(2, projects.size());
    }

    @Test
    void getProjectsByAdmin_shouldReturnProjects() {
        when(projectRepository.findByAdminId(1L)).thenReturn(List.of(new Project()));

        List<Project> result = projectService.getProjectsByAdmin(1L);

        assertEquals(1, result.size());
    }


    @Test
    void getProjectsByUser_shouldMergeProjects() {
        Project adminProject = new Project("AdminP", "desc", LocalDate.now(), new User());
        Project memberProject = new Project("MemberP", "desc", LocalDate.now(), new User());

        when(projectRepository.findByAdminId(1L)).thenReturn(List.of(adminProject));
        when(projectRepository.findByUserIsMember(1L)).thenReturn(List.of(memberProject));

        List<Project> result = projectService.getProjectsByUser(1L);

        assertEquals(2, result.size());
    }


    @Test
    void addMemberToProject_shouldAddUserWithDefaultRole() {
        Project project = new Project("Nom", "desc", LocalDate.now(), new User());
        User user = new User("user", "user@mail.com", "1234");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(projectMemberRepository.existsByProjectAndUser(project, user)).thenReturn(false);

        Project result = projectService.addMemberToProject(1L, "user@mail.com", null);

        assertEquals(project, result);
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }


    @Test
    void addMemberToProject_shouldThrow_whenAlreadyMember() {
        Project project = new Project();
        User user = new User();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("mail")).thenReturn(Optional.of(user));
        when(projectMemberRepository.existsByProjectAndUser(project, user)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            projectService.addMemberToProject(1L, "mail", null));

        assertTrue(ex.getMessage().contains("déjà membre"));
    }


    @Test
    void getProjectMembers_shouldReturnList() {
        Project project = new Project();
        User user = new User("u", "u@mail.com", "pwd");
        user.setId(1L);
        ProjectMember pm = new ProjectMember(project, user, ProjectRole.MEMBER);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProject(project)).thenReturn(List.of(pm));

        List<ProjectMemberDTO> result = projectService.getProjectMembers(1L);

        assertEquals(1, result.size());
        assertEquals("u", result.get(0).getUsername());
    }


    @Test
    void getProjectById_shouldReturnProject_whenExists() {
        Project project = new Project();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Optional<Project> result = projectService.getProjectById(1L);

        assertTrue(result.isPresent());
    }


    @Test
    void deleteProject_shouldReturnTrue_whenDeleted() {
        Project project = new Project();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        boolean result = projectService.deleteProjectById(1L);

        assertTrue(result);
        verify(projectRepository).deleteById(1L);
    }


    @Test
    void deleteProject_shouldReturnFalse_whenNotFound() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = projectService.deleteProjectById(999L);

        assertEquals(false, result);
    }


    @Test
    void assignRole_shouldSaveProjectMember() {
        Project project = new Project();
        User user = new User();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        projectService.assignRole(1L, 2L, ProjectRole.ADMIN);

        verify(projectMemberRepository).save(any(ProjectMember.class));
    }


    @Test
    void assignRole_shouldThrow_ifProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            projectService.assignRole(1L, 2L, ProjectRole.MEMBER));

        assertTrue(ex.getMessage().contains("Projet non trouvé"));
    }










}

