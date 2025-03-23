package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.model.TaskPriority;

public class TaskRequest {
    private Long userId;
    private String name;
    private String description;
    private LocalDate dueDate;
    private TaskPriority priority;
    private Long assigneeId; // Peut être null
    private LocalDate deadline ; 
    

    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public TaskPriority getPriority() { return priority; }
    public Long getAssigneeId() { return assigneeId; }
    public LocalDate getDeadLine() {return deadline; }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}


