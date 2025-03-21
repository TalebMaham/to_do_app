package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.Project;
import com.example.demo.model.ProjectRole;
import com.example.demo.model.Task;
import com.example.demo.model.TaskHistory;
import com.example.demo.model.TaskPriority;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectMemberRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskHistoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository ;
    private final TaskHistoryRepository taskHistoryRepository;
    private final EmailService emailService; // ✅ Injecter EmailService

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository, UserRepository userRepository, TaskHistoryRepository taskHistoryRepository, EmailService emailService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskHistoryRepository = taskHistoryRepository;
        this.emailService = emailService;
    }

    // Créer une tâche sans obligation d'assignation immédiate
    public Task createTask(Long projectId, Long userId, String name, String description, LocalDate dueDate, TaskPriority priority, Long assigneeId, LocalDate deadline) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec l'ID : " + projectId));

        Task task = new Task(name, description, dueDate, priority, project);

        // Vérifier si un utilisateur est assigné
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur assigné introuvable"));

            boolean isMember = project.getProjectMembers().stream()
                    .anyMatch(pm -> pm.getUser().getId().equals(assigneeId));

            if (project.getAdmin().getId() == assigneeId)
            {
                isMember = true ; 
            }

            if (!isMember) {
                throw new RuntimeException("L'utilisateur assigné ne fait pas partie du projet !");
            }
            task.assignUser(assignee);
        if (deadline != null) {
            task.setDeadline(deadline);
        }
        }

        return taskRepository.save(task);
    }

 public Task assignTask(Long taskId, Long assigneeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tâche introuvable avec l'ID : " + taskId));

        Project project = task.getProject();

        // Vérifier si l'utilisateur est membre du projet
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        boolean isMember = project.getProjectMembers().stream()
                .anyMatch(pm -> pm.getUser().getId().equals(assigneeId));

        if (project.getAdmin().getId().equals(assigneeId)) {
            isMember = true;
        }

        if (!isMember) {
            throw new RuntimeException("L'utilisateur assigné ne fait pas partie du projet !");
        }

        task.assignUser(assignee);
        taskRepository.save(task);

        // ✅ Envoyer un e-mail à l'utilisateur assigné
        try {
            String subject = "Nouvelle tâche assignée : " + task.getName();
            String message = "<p>Bonjour " + assignee.getUsername() + ",</p>"
                    + "<p>Vous avez été assigné à la tâche : <strong>" + task.getName() + "</strong></p>"
                    + "<p>Description : " + task.getDescription() + "</p>"
                    + "<p>Date limite : " + (task.getDueDate() != null ? task.getDueDate().toString() : "Non spécifiée") + "</p>"
                    + "<p>Merci de vérifier votre tableau de bord.</p>";

            emailService.sendEmail(assignee.getEmail(), subject, message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }

        return task;
    }

    public List<Task> getTasksByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec l'ID : " + projectId));

        return taskRepository.findByProject(project);
    }

    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }



    public Task updateTask(Long taskId, Map<String, Object> updates, Long userId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + taskId));
    
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
    
        Project project = task.getProject();
    
        // Vérification améliorée de l'autorisation
        boolean isAuthorized = project.getAdmin().getId().equals(userId) ||
            project.getProjectMembers().stream().anyMatch(pm -> 
                pm.getUser().getId().equals(userId) && !pm.getRole().equals(ProjectRole.OBSERVER));
    
        if (!isAuthorized) {
            throw new RuntimeException("Vous n'avez pas le droit de modifier cette tâche.");
        }
    
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    saveHistory(task, "name", task.getName(), (String) value, user);
                    task.setName((String) value);
                    break;
                case "description":
                    saveHistory(task, "description", task.getDescription(), (String) value, user);
                    task.setDescription((String) value);
                    break;
                case "dueDate":
                    saveHistory(task, "dueDate", task.getDueDate().toString(), value.toString(), user);
                    task.setDueDate(LocalDate.parse(value.toString()));
                    break;
                case "priority":
                    saveHistory(task, "priority", task.getPriority().name(), (String) value, user);
                    task.setPriority(TaskPriority.valueOf((String) value));
                    break;
                case "deadLine":
                    saveHistory(task, "deadLine", task.getDeadline() != null ? task.getDeadline().toString() : "null", value.toString(), user);
                    task.setDeadline(LocalDate.parse(value.toString()));
                    break;
                case "status":
                    saveHistory(task, "status", task.getStatus().name(), (String) value, user);
                    task.setStatus(TaskStatus.valueOf((String) value));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });
    
        return taskRepository.save(task);
    }
    
    private void saveHistory(Task task, String field, String oldValue, String newValue, User user) {
        if (!oldValue.equals(newValue)) { 
            TaskHistory history = new TaskHistory(task, field, oldValue, newValue, user);
            taskHistoryRepository.save(history);
        }
    }

}

