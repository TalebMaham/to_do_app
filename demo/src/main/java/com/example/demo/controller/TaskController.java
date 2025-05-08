package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.TaskRequest;
import com.example.demo.model.Task;
import com.example.demo.model.TaskHistory;
import com.example.demo.repository.TaskHistoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200") 
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskHistoryRepository taskHistoryRepository;
    private final TaskRepository taskRepository ; 

    public TaskController(TaskService taskService, TaskHistoryRepository taskHistoryRepository, TaskRepository taskRepository ) {
        this.taskService = taskService;
        this.taskHistoryRepository = taskHistoryRepository;
        this.taskRepository = taskRepository ; 
    }


    @PostMapping
    public ResponseEntity<Task> createTask(
            @PathVariable Long projectId,
            @RequestBody TaskRequest request) {

        Task task = taskService.createTask(
                projectId, 
                request.getUserId(), 
                request.getName(), 
                request.getDescription(), 
                request.getDueDate(), 
                request.getPriority(),
                request.getAssigneeId(),
                request.getDeadLine() 
        );

        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}/assign")
    public ResponseEntity<Task> assignTask(
            @PathVariable Long taskId,
            @RequestParam Long assigneeId) {

        Task task = taskService.assignTask(taskId, assigneeId);
        return ResponseEntity.ok(task);
    }
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

        // Récupérer une tâche par son ID
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
            return taskService.getTaskById(taskId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

    
    /*
     * Mise à jour partielle d'une tache .
     */
    @PatchMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
        @PathVariable Long projectId,
        @PathVariable Long taskId, 
        @RequestBody Map<String, Object> updates,
        @RequestParam Long userId // L'ID de l'utilisateur qui effectue la modification
    ){
        Task updatedTask = taskService.updateTask(taskId, updates, userId);
        return ResponseEntity.ok(updatedTask);
    }
    


@GetMapping("/{taskId}/history")
public ResponseEntity<?> getTaskHistory(@PathVariable Long taskId) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    
    List<TaskHistory> history = taskHistoryRepository.findByTask(task);
    return ResponseEntity.ok(history);
}


}

