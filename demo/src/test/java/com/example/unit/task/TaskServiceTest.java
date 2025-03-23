package com.example.unit.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.Project;
import com.example.demo.model.Task;
import com.example.demo.model.TaskPriority;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskHistoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TaskService;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private TaskHistoryRepository taskHistoryRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_shouldReturnTask_whenValidData() {
        Long projectId = 1L;
        Long creatorId = 2L;

        User creator = new User("sidi", "sidi@email.com", "pass");
        Project project = new Project("Nom", "desc", LocalDate.now(), creator);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        Task task = taskService.createTask(
                projectId, creatorId, "Ma tâche", "Desc", LocalDate.now(), TaskPriority.HIGH, null, null);

        assertEquals("Ma tâche", task.getName());
        assertEquals(project, task.getProject());
    }

    @Test
    void createTask_shouldThrow_whenProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.createTask(1L, 2L, "X", "Y", LocalDate.now(), TaskPriority.LOW, null, null));
    }

    @Test
    void assignTask_shouldThrow_whenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> 
            taskService.assignTask(99L, 1L));

        assertTrue(ex.getMessage().contains("Tâche introuvable"));
    }

    @Test
    void assignTask_shouldThrow_whenUserNotFound() {
        Task task = new Task();
        Project project = new Project();
        project.setAdmin(new User("admin", "a@a.com", "pass"));
        task.setProject(project);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            taskService.assignTask(1L, 999L));

        assertTrue(ex.getMessage().contains("Utilisateur introuvable"));
    }

    @Test
    void assignTask_shouldThrow_whenUserNotProjectMember() {
        User outsider = new User("outsider", "o@o.com", "pwd");
        outsider.setId(3L);

        User admin = new User("admin", "a@a.com", "pwd");
        admin.setId(1L);

        Project project = new Project("P", "Desc", LocalDate.now(), admin);
        project.setId(10L);

        Task task = new Task("Tâche", "desc", LocalDate.now(), TaskPriority.MEDIUM, project);
        task.setProject(project);
        task.setId(5L);

        when(taskRepository.findById(5L)).thenReturn(Optional.of(task));
        when(userRepository.findById(3L)).thenReturn(Optional.of(outsider));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            taskService.assignTask(5L, 3L));

        assertTrue(ex.getMessage().contains("ne fait pas partie du projet"));
    }


    @Test
    void getTaskById_shouldReturnTask_whenExists() {
        Task task = new Task();
        task.setId(10L);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        Optional<Task> result = taskService.getTaskById(10L);
        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
    }

    @Test
    void createTask_shouldAssignUser_whenMemberOfProject() {
        Long projectId = 1L;
        Long creatorId = 2L;
        Long assigneeId = 3L;

        User creator = new User("creator", "creator@mail.com", "123");
        creator.setId(creatorId);

        User assignee = new User("assignee", "assign@mail.com", "pwd");
        assignee.setId(assigneeId);

        Project project = new Project("Nom", "desc", LocalDate.now(), creator);
        project.setId(projectId);
        project.getProjectMembers().add(new com.example.demo.model.ProjectMember(project, assignee, com.example.demo.model.ProjectRole.MEMBER));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Task task = taskService.createTask(projectId, creatorId, "Tâche", "desc", LocalDate.now(), TaskPriority.MEDIUM, assigneeId, LocalDate.now());

        assertEquals(assignee, task.getAssignee());
        assertEquals("Tâche", task.getName());
        assertEquals(project, task.getProject());
    }


    @Test
    void createTask_shouldThrow_whenAssigneeNotFound() {
        Long projectId = 1L;
        Long creatorId = 2L;
        Long assigneeId = 3L;

        User creator = new User("creator", "creator@mail.com", "123");
        creator.setId(creatorId);

        Project project = new Project("Nom", "desc", LocalDate.now(), creator);
        project.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            taskService.createTask(projectId, creatorId, "Tâche", "desc", LocalDate.now(), TaskPriority.MEDIUM, assigneeId, LocalDate.now()));

        assertTrue(ex.getMessage().contains("Utilisateur assigné introuvable"));
    }


    @Test
    void createTask_shouldThrow_whenAssigneeNotProjectMember() {
        Long projectId = 1L;
        Long creatorId = 2L;
        Long assigneeId = 3L;

        User creator = new User("creator", "creator@mail.com", "123");
        creator.setId(creatorId);

        User assignee = new User("assignee", "assign@mail.com", "pwd");
        assignee.setId(assigneeId);

        Project project = new Project("Nom", "desc", LocalDate.now(), creator);
        project.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            taskService.createTask(projectId, creatorId, "Tâche", "desc", LocalDate.now(), TaskPriority.MEDIUM, assigneeId, LocalDate.now()));

        assertTrue(ex.getMessage().contains("ne fait pas partie du projet"));
    }


    @Test
    void createTask_shouldAssign_whenAssigneeIsAdmin() {
        Long projectId = 1L;
        Long adminId = 2L;

        User admin = new User("admin", "admin@mail.com", "123");
        admin.setId(adminId);

        Project project = new Project("Nom", "desc", LocalDate.now(), admin);
        project.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Task task = taskService.createTask(projectId, adminId, "Tâche", "desc", LocalDate.now(), TaskPriority.LOW, adminId, null);

        assertEquals(admin, task.getAssignee());
    }

    @Test
void updateTask_shouldUpdateNameAndTrackHistory_whenAdmin() {
    Long taskId = 1L;
    Long userId = 2L;

    User admin = new User("admin", "admin@mail.com", "123");
    admin.setId(userId);

    Project project = new Project("P", "desc", LocalDate.now(), admin);
    Task task = new Task("Old Name", "desc", LocalDate.now(), TaskPriority.MEDIUM, project);
    task.setId(taskId);

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(userRepository.findById(userId)).thenReturn(Optional.of(admin));
    when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

    Map<String, Object> updates = Map.of("name", "New Name");

    Task updated = taskService.updateTask(taskId, updates, userId);

    assertEquals("New Name", updated.getName());
}

@Test
void updateTask_shouldFail_ifUserIsObserver() {
    Long userId = 1L;
    Long taskId = 2L;

    User observer = new User("obs", "obs@mail.com", "pwd");
    observer.setId(userId);
    User admin = new User("admin", "admin@mail.com", "123");
    admin.setId(99L);

    Project project = new Project("P", "desc", LocalDate.now(), admin);
    Task task = new Task("T", "desc", LocalDate.now(), TaskPriority.MEDIUM, project);

    // Ajout du membre observer
    project.getProjectMembers().add(new com.example.demo.model.ProjectMember(project, observer, com.example.demo.model.ProjectRole.OBSERVER));

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(userRepository.findById(userId)).thenReturn(Optional.of(observer));

    Map<String, Object> updates = Map.of("name", "Nouvelle");

    RuntimeException ex = assertThrows(RuntimeException.class, () ->
        taskService.updateTask(taskId, updates, userId));

    assertTrue(ex.getMessage().contains("n'avez pas le droit"));
}

@Test
void updateTask_shouldThrowIfTaskNotFound() {
    when(taskRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () ->
        taskService.updateTask(1L, Map.of("name", "X"), 1L));
}

@Test
void updateTask_shouldThrowIfUserNotFound() {
    Task task = new Task();
    task.setId(1L);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () ->
        taskService.updateTask(1L, Map.of("name", "X"), 99L));
}

@Test
void updateTask_shouldUpdateAllFields() {
    Long userId = 1L;
    User admin = new User("admin", "admin@mail.com", "123");
    admin.setId(userId);

    Project project = new Project("P", "desc", LocalDate.now(), admin);
    Task task = new Task("Old", "Old desc", LocalDate.now(), TaskPriority.LOW, project);
    task.setId(1L);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
    when(userRepository.findById(userId)).thenReturn(Optional.of(admin));
    when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    Map<String, Object> updates = Map.of(
        "name", "New Name",
        "description", "New Desc",
        "dueDate", LocalDate.now().plusDays(5).toString(),
        "priority", TaskPriority.HIGH.name(),
        "deadLine", LocalDate.now().plusDays(3).toString(),
        "status", TaskStatus.DONE.name()
    );

    Task updated = taskService.updateTask(1L, updates, userId);

    assertEquals("New Name", updated.getName());
    assertEquals("New Desc", updated.getDescription());
    assertEquals(TaskPriority.HIGH, updated.getPriority());
    assertEquals(TaskStatus.DONE, updated.getStatus());
}

@Test
void updateTask_shouldThrowForInvalidField() {
    Long userId = 1L;
    User admin = new User("admin", "admin@mail.com", "123");
    admin.setId(userId);

    Project project = new Project("P", "desc", LocalDate.now(), admin);
    Task task = new Task("Old", "desc", LocalDate.now(), TaskPriority.LOW, project);
    task.setId(1L);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
    when(userRepository.findById(userId)).thenReturn(Optional.of(admin));

    Map<String, Object> updates = Map.of("unknown", "oops");

    assertThrows(IllegalArgumentException.class, () ->
        taskService.updateTask(1L, updates, userId));
}










}

