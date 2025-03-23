package com.example.integration.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.DemoApplication;
import com.example.demo.model.Project;
import com.example.demo.model.Task;
import com.example.demo.model.TaskPriority;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TaskService;

@SpringBootTest(classes = DemoApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class TaskServiceIntegrationTest {

    @Autowired private TaskService taskService;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;

    
    @Transactional
    @BeforeEach
    void cleanDb() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createTask_shouldPersistTask() {
        User admin = userRepository.save(new User("admin", "a@a.com", "pass"));
        Project project = projectRepository.save(new Project("Projet", "desc", LocalDate.now(), admin));

        Task task = taskService.createTask(
                project.getId(), admin.getId(), "Tâche", "desc", LocalDate.now(), TaskPriority.HIGH, null, null);

        assertNotNull(task.getId());
        assertEquals("Tâche", task.getName());
        assertEquals(project.getId(), task.getProject().getId());
    }

    @Test
    void getTaskById_shouldReturnTask() {
        User user = userRepository.save(new User("u", "u@u.com", "p"));
        Project project = projectRepository.save(new Project("Projet", "desc", LocalDate.now(), user));
        Task task = taskService.createTask(project.getId(), user.getId(), "Tâche", "desc", LocalDate.now(), TaskPriority.HIGH, null, null);

        assertEquals(task.getId(), taskService.getTaskById(task.getId()).get().getId());
    }

    @Test
    void getTaskById_shouldReturnEmpty_whenNotFound() {
        assertEquals(false, taskService.getTaskById(999L).isPresent());
    }

    @Test
    void getTasksByProject_shouldReturnEmptyList_whenNoTasks() {
        User user = userRepository.save(new User("u", "u@u.com", "p"));
        Project project = projectRepository.save(new Project("P", "d", LocalDate.now(), user));
        
        assertEquals(0, taskService.getTasksByProject(project.getId()).size());
    }

    @Test
    void updateTask_shouldModifyDescription() {
        User user = userRepository.save(new User("u", "u@u.com", "p"));
        Project project = projectRepository.save(new Project("P", "d", LocalDate.now(), user));
        Task task = taskService.createTask(project.getId(), user.getId(), "Tâche", "desc", LocalDate.now(), TaskPriority.MEDIUM, null, null);

        Map<String, Object> updates = Map.of("description", "Nouvelle desc");

        Task updated = taskService.updateTask(task.getId(), updates, user.getId());

        assertEquals("Nouvelle desc", updated.getDescription());
    }




}
