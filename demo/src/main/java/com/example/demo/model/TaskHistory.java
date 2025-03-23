package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "task_history")
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    
    private Task task;

    @Column(nullable = false)
    private String fieldChanged;

    @Column(nullable = false, length = 1000)
    private String oldValue;

    @Column(nullable = false, length = 1000)
    private String newValue;

    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "modified_by", nullable = false)
    private User modifiedBy;

    public TaskHistory() {}

    public TaskHistory(Task task, String fieldChanged, String oldValue, String newValue, User modifiedBy) {
        this.task = task;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.modifiedAt = LocalDateTime.now();
        this.modifiedBy = modifiedBy;
    }


    public Long getId() {
        return id;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public Task getTask() {
        return task;
    }

    public String getFieldChanged() {
        return fieldChanged;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getOldValue() {
        return oldValue;
    }

   
}
