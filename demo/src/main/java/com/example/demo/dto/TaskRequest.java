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
}


